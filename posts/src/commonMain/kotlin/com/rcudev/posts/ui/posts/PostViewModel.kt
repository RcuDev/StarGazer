package com.rcudev.posts.ui.posts

import androidx.compose.runtime.mutableIntStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcudev.posts.data.remote.PostService
import com.rcudev.posts.model.Post
import com.rcudev.posts.model.PostType
import com.rcudev.posts.ui.ViewState
import com.rcudev.storage.POST_TYPE_FILTER
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PostViewModel(
    private val preferences: DataStore<Preferences>,
    private val postService: PostService
) : ViewModel() {

    private val posts = MutableStateFlow<List<Post>?>(null)
    private val loadingNextPage = MutableStateFlow(false)
    private val showError = MutableStateFlow(false)
    private val showLoadPageError = MutableStateFlow(false)
    private val nextPageToLoad = mutableIntStateOf(0)
    val newsSites = MutableStateFlow("")

    val state = combine(
        posts,
        loadingNextPage,
        showError,
        showLoadPageError,
    ) { posts, loadingPage, showError, showLoadPageError ->
        when {
            showError -> ViewState.Error
            posts?.isEmpty() == true -> ViewState.Empty("No articles found")
            posts?.isNotEmpty() == true -> ViewState.Success(
                posts = posts,
                loadingNextPage = loadingPage,
                loadingError = showLoadPageError
            )

            else -> ViewState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ViewState.Loading
    )

    init {
        viewModelScope.launch {
            preferences.data.collectLatest { prefs ->
                val postType = PostType.entries.find { it.type == prefs[POST_TYPE_FILTER] }
                postType?.let {
                    posts.value = null
                    loadPosts(postType = postType)
                }
            }
        }
    }

    private fun loadPosts(
        page: Int = 0,
        postType: PostType = PostType.ARTICLES,
        newsSites: String = ""
    ) = viewModelScope.launch {
        val offset = if (page == 0) 0 else (page * 10)
        postService.getArticles(
            postType = postType,
            offset = offset,
            newsSites = newsSites
        ).fold(
            onSuccess = { data ->
                posts.update { ((it ?: emptyList()) + (data.results)) }
                loadingNextPage.value = false
            },
            onFailure = { showError.value = true }
        )
    }

    fun loadNextPage() {
        if (!loadingNextPage.value) {
            loadingNextPage.value = true
            nextPageToLoad.value += 1
            loadPosts(
                page = nextPageToLoad.value,
                newsSites = newsSites.value
            )
        }
    }
}