package com.rcudev.posts.ui.posts

import androidx.compose.runtime.Stable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcudev.posts.domain.InfoService
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.domain.model.Post
import com.rcudev.posts.domain.model.PostType
import com.rcudev.posts.ui.ViewState
import com.rcudev.storage.NEWS_SITES_FILTER
import com.rcudev.storage.POST_TYPE_FILTER
import com.rcudev.utils.logMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Stable
class PostViewModel(
    private val preferences: DataStore<Preferences>,
    private val postService: PostService,
    private val infoService: InfoService
) : ViewModel() {

    private val posts = MutableStateFlow<List<Post>?>(null)
    private val loadingNextPage = MutableStateFlow(false)
    private val showError = MutableStateFlow(false)
    private val showLoadPageError = MutableStateFlow(false)
    private val nextPageToLoad = MutableStateFlow(0)
    
    val newsSites = MutableStateFlow(emptyList<String>())
    val newsSitesSelected = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
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
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ViewState.Loading
    )

    init {
        viewModelScope.launch {
            loadInfo()
        }
        
        viewModelScope.launch {
            preferences.data
                .distinctUntilChanged()
                .collect { prefs ->
                    val newsSitesFilter = prefs[NEWS_SITES_FILTER]
                    val postType = PostType.fromString(prefs[POST_TYPE_FILTER])
                    
                    if (postType != null) {
                        newsSitesSelected.value = newsSitesFilter.orEmpty()
                        resetAndLoadPosts(postType, newsSitesSelected.value)
                    } else {
                        preferences.edit { it[POST_TYPE_FILTER] = PostType.ARTICLES.type }
                    }
                }
        }
    }

    private suspend fun resetAndLoadPosts(postType: PostType, newsSites: String) {
        posts.value = null
        nextPageToLoad.value = 0
        showError.value = false
        loadPosts(postType = postType, newsSites = newsSites)
    }

    private suspend fun loadPosts(
        page: Int = 0,
        postType: PostType = PostType.ARTICLES,
        newsSites: String = ""
    ) {
        val offset = if (page == 0) 0 else (page * 10)
        
        postService.getArticles(
            postType = postType,
            offset = offset,
            newsSites = newsSites
        ).fold(
            onSuccess = { data ->
                posts.update { currentPosts ->
                    if (page == 0) data.results else (currentPosts.orEmpty() + data.results)
                }
                loadingNextPage.value = false
                showLoadPageError.value = false
            },
            onFailure = { throwable ->
                logMessage("PostViewModel", throwable.message ?: "Unknown error")
                if (page == 0) {
                    showError.value = true
                } else {
                    showLoadPageError.value = true
                }
                loadingNextPage.value = false
            }
        )
    }

    private suspend fun loadInfo() {
        infoService.getInfo().fold(
            onSuccess = { data ->
                newsSites.value = data.newsSites
            },
            onFailure = { 
                // Silently fail for info loading
            }
        )
    }

    fun loadNextPage() {
        if (loadingNextPage.value) return
        
        viewModelScope.launch {
            loadingNextPage.value = true
            nextPageToLoad.update { it + 1 }
            
            val currentState = state.value
            val postType = if (currentState is ViewState.Success) {
                currentState.posts.firstOrNull()?.postType ?: PostType.ARTICLES
            } else {
                PostType.ARTICLES
            }
            
            loadPosts(
                page = nextPageToLoad.value,
                postType = postType,
                newsSites = newsSitesSelected.value
            )
        }
    }
}