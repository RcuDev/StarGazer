package com.rcudev.posts.ui.posts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import com.rcudev.posts.domain.InfoService
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.NEWS_SITES_FILTER
import com.rcudev.storage.POST_TYPE_FILTER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostPresenter(
    private val postService: PostService,
    private val infoService: InfoService,
    private val preferences: DataStore<Preferences>,
    private val scope: CoroutineScope
) {

    private val events = MutableSharedFlow<PostEvent>()

    val state: StateFlow<PostState> = moleculeFlow(RecompositionMode.Immediate) {
        postPresenterLogic(
            events = events,
            postService = postService,
            infoService = infoService,
            preferences = preferences
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PostState()
    )

    fun sendEvent(event: PostEvent) {
        scope.launch {
            events.emit(event)
        }
    }
}

@Composable
private fun postPresenterLogic(
    events: Flow<PostEvent>,
    postService: PostService,
    infoService: InfoService,
    preferences: DataStore<Preferences>
): PostState {

    var state by remember { mutableStateOf(PostState()) }

    // Load initial configuration from DataStore
    val postTypePreference by preferences.data
        .map { prefs ->
            prefs[POST_TYPE_FILTER] ?: PostType.ARTICLES.type
        }
        .collectAsState(initial = PostType.ARTICLES.type)

    val newsSitePreferences by preferences.data
        .map { prefs ->
            prefs[NEWS_SITES_FILTER] ?: ""
        }.collectAsState(initial = "")

    // Load news sites
    LaunchedEffect(Unit) {
        infoService.getInfo().fold(
            onSuccess = { data ->
                state = state.copy(
                    newsSites = data.newsSites,
                    newsSiteSelected = newsSitePreferences.takeIf { it.isNotEmpty() } ?: data.newsSites.firstOrNull() ?: "",
                    postTypeSelected = PostType.entries.find { it.type == postTypePreference } ?: PostType.ARTICLES
                )
            }, onFailure = {
                // No-op
            }
        )
    }

    // Handle events
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is PostEvent.LoadPost -> {
                    state = state.copy(isLoading = true, error = null)
                    state = loadPosts(state, postService)
                }
                is PostEvent.LoadNextPage -> {
                    if (state.hasMorePages && !state.loadingNextPage) {
                        state = state.copy(loadingNextPage = true)
                        state = loadPosts(state, postService)
                    }
                }
                is PostEvent.OnPostClick -> {
                    // TODO: only log for now
                    println("Post clicked: ${event.url}")
                }
                is PostEvent.SelectNewsSite -> {
                    if (state.newsSiteSelected != event.site) {
                        state = state.copy(
                            newsSiteSelected = event.site,
                            posts = emptyList(),
                            hasMorePages = true
                        )

                        // Save new news site
                        preferences.edit {
                            it[NEWS_SITES_FILTER] = event.site
                        }

                        state = loadPosts(
                            state = state,
                            postService = postService
                        )
                    }
                }
                is PostEvent.SelectPostType -> {
                    if (state.postTypeSelected != event.post) {
                        state = state.copy(
                            postTypeSelected = event.post,
                            posts = emptyList(),
                            hasMorePages = true
                        )

                        // Save new type of post
                        preferences.edit {
                            it[POST_TYPE_FILTER] = event.post.type
                        }

                        state = loadPosts(
                            state = state,
                            postService = postService
                        )
                    }
                }
            }
        }
    }

    return state
}

private suspend fun loadPosts(
    state: PostState,
    postService: PostService
): PostState {

    var loadPostsState = state.copy()

    postService.getArticles(
        postType = state.postTypeSelected,
        offset = state.posts.size,
        newsSites = state.newsSiteSelected
    ).fold(
        onSuccess = { posts ->
            loadPostsState = state.copy(
                posts = state.posts + posts.results,
                isLoading = false,
                hasMorePages = posts.results.isNotEmpty()
            )
        }, onFailure = {
            loadPostsState = state.copy(
                isLoading = false,
                error = it.message
            )
        }
    )

    return loadPostsState
}