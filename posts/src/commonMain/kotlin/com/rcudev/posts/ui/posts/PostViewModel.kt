package com.rcudev.posts.ui.posts

import androidx.compose.runtime.mutableIntStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcudev.posts.data.remote.InfoService
import com.rcudev.posts.data.remote.PostService
import com.rcudev.posts.model.Post
import com.rcudev.posts.model.PostType
import com.rcudev.posts.ui.ViewState
import com.rcudev.storage.NEWS_SITES_FILTER
import com.rcudev.storage.POST_TYPE_FILTER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PostViewModel(
    private val preferences: DataStore<Preferences>,
    private val postService: PostService,
    private val infoService: InfoService
) : ViewModel() {

    private val posts = MutableStateFlow<List<Post>?>(null)
    private val postTypeSelected = MutableStateFlow(PostType.ARTICLES)
    private val loadingNextPage = MutableStateFlow(false)
    private val showError = MutableStateFlow(false)
    private val showLoadPageError = MutableStateFlow(false)
    private val nextPageToLoad = mutableIntStateOf(0)
    val newsSites = MutableStateFlow(emptyList<String>())
    val newsSitesSelected = MutableStateFlow("")

    val state = combine(
        posts,
        loadingNextPage,
        showError,
        showLoadPageError,
    ) { posts, loadingPage, showError, showLoadPageError ->
        when {
            showError -> ViewState.Error
            posts?.isEmpty() == true -> ViewState.Empty("No post found")
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
            loadInfo()
            preferences.data
                .distinctUntilChangedBy { it }
                .collectLatest { prefs ->
                    val newsSitesFilter = prefs[NEWS_SITES_FILTER]
                    val postType = PostType.entries.find { it.type == prefs[POST_TYPE_FILTER] }
                    postType?.let {
                        newsSitesSelected.value = newsSitesFilter.orEmpty()
                        postTypeSelected.value = postType
                        posts.value = null
                        loadPosts(
                            postType = postTypeSelected.value,
                            newsSites = newsSitesSelected.value
                        )
                    } ?: preferences.edit { it[POST_TYPE_FILTER] = postTypeSelected.value.type }
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

    private fun loadInfo() = viewModelScope.launch {
        infoService.getInfo()
            .fold(
                onSuccess = { data ->
                    newsSites.value = data.newsSites
                }, onFailure = {
                    // No-op
                }
            )
    }

    fun loadNextPage() {
        if (!loadingNextPage.value) {
            loadingNextPage.value = true
            nextPageToLoad.value += 1
            loadPosts(
                page = nextPageToLoad.value,
                postType = postTypeSelected.value,
                newsSites = newsSitesSelected.value
            )
        }
    }
}