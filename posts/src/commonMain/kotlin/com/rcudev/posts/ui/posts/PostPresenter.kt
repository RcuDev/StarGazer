package com.rcudev.posts.ui.posts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.domain.model.Post
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.NEWS_SITES_FILTER
import com.rcudev.storage.POST_TYPE_FILTER
import com.rcudev.utils.logMessage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy

class PostPresenter(
    private val preferences: DataStore<Preferences>,
    private val postService: PostService,
) {

    @Composable
    fun present(events: Flow<PostEvent>): Pair<PostState, Flow<PostEffect>> {

        // Mutable local state (private to presenter)
        var posts by remember { mutableStateOf<List<Post>?>(null) }
        var postTypeSelected by remember { mutableStateOf(PostType.ARTICLES) }
        var newsSitesSelected by remember { mutableStateOf("") }
        var loadingNextPage by remember { mutableStateOf(false) }
        var showError by remember { mutableStateOf(false) }
        var showLoadPageError by remember { mutableStateOf(false) }
        var nextPageToLoad by remember { mutableIntStateOf(0) }

        val effectChannel = remember {
            MutableSharedFlow<PostEffect>(
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_LATEST
            )
        }

        // DataStore preferences observer
        LaunchedEffect(Unit) {
            preferences.data
                .distinctUntilChangedBy { it }
                .collectLatest { prefs ->
                    val postTypeFilter = PostType.entries.find { it.type == prefs[POST_TYPE_FILTER] } ?: PostType.ARTICLES
                    val newsSiteFilter = prefs[NEWS_SITES_FILTER]

                    newsSitesSelected = newsSiteFilter.orEmpty()
                    postTypeSelected = postTypeFilter
                    posts = null // Reset Posts
                    nextPageToLoad = 0 // Reset page
                    showError = false
                    showLoadPageError = false

                    // Load post with updated filters
                    loadPosts(
                        page = 0,
                        postType = postTypeSelected,
                        newsSitesFilter = newsSitesSelected,
                        onSuccess = { newPosts ->
                            posts = newPosts
                        },
                        onError = {
                            showError = true
                        }
                    )
                }
        }

        // Handle user events
        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is PostEvent.OnPostClick -> effectChannel.tryEmit(PostEffect.NavigateToWebView(event.url))
                    PostEvent.DismissLoadPageError -> showLoadPageError = false
                    PostEvent.LoadNextPage -> {
                        if (!loadingNextPage && posts != null) {
                            loadingNextPage = true
                            nextPageToLoad += 1

                            loadPosts(
                                page = nextPageToLoad,
                                postType = postTypeSelected,
                                newsSitesFilter = newsSitesSelected,
                                onSuccess = { newPosts ->
                                    posts = (posts ?: emptyList()) + newPosts
                                    loadingNextPage = false
                                },
                                onError = {
                                    showLoadPageError = true
                                    loadingNextPage = false
                                }
                            )
                        }

                    }
                    PostEvent.Retry -> {
                        posts = null
                        showError = false
                        nextPageToLoad = 0

                        loadPosts(
                            page = 0,
                            postType = postTypeSelected,
                            newsSitesFilter = newsSitesSelected,
                            onSuccess = { newPosts ->
                                posts = newPosts
                            },
                            onError = {
                                showError = true
                            }
                        )
                    }
                }
            }
        }

        val state = when {
            showError -> PostState.Error
            posts == null -> PostState.Loading
            posts?.isEmpty() == true -> PostState.Empty("No posts found")
            posts?.isNotEmpty() == true -> PostState.Content(
                posts = posts!!,
                loadingNextPage = loadingNextPage,
                showLoadPageError = showLoadPageError
            )
            else -> PostState.Loading
        }

        return state to effectChannel
    }

    private suspend fun loadPosts(
        page: Int,
        postType: PostType,
        newsSitesFilter: String,
        onSuccess: (List<Post>) -> Unit,
        onError: () -> Unit
    ) {
        val offset = if (page == 0) 0 else (page * 10)

        postService.getArticles(
            postType = postType,
            offset = offset,
            newsSites = newsSitesFilter
        ).fold(
            onSuccess = { data -> onSuccess(data.results) },
            onFailure = { error ->
                logMessage("PostPresenter", error.message ?: "Unknown error")
                onError()
            }
        )

    }
}