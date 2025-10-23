package com.rcudev.posts.ui.posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rcudev.posts.ui.settings.FilterDropDown

@Composable
internal fun PostScreen(
    presenter: PostPresenter,
    state: PostState,
    onEvent: (PostEvent) -> Unit,
    showSettings: () -> Boolean,
    hideSettings: () -> Unit,
    showSnackBar: (String) -> Unit,
    onPostClick: (String) -> Unit,
    finishSplash: () -> Unit = {}
) {

    LaunchedEffect(Unit) {
        onEvent(PostEvent.LoadPost)
    }

    LaunchedEffect(Unit) {
        presenter.effects.collect { effect ->
            when (effect) {
                is PostEffect.NavigateToUrl -> onPostClick(effect.url)
                is PostEffect.ShowError -> showSnackBar(effect.message)
            }
        }
    }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            finishSplash()
        }
    }

    val viewState = remember(state) {
        when {
            state.error != null -> ViewState.Error
            state.posts.isEmpty() && !state.isLoading -> ViewState.Empty("No post found")
            state.posts.isNotEmpty() -> ViewState.Success(
                posts = state.posts,
                loadingNextPage = state.loadingNextPage,
                loadingError = false
            )
            else -> ViewState.Loading
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PostContent(
            viewState = viewState,
            loadNextPage = {
                onEvent(PostEvent.LoadNextPage)
            },
            showSnackBar = {
                showSnackBar("Loading more post")
            },
            onItemClick = { url ->
                onEvent(PostEvent.OnPostClick(url))
            }
        )

        if (showSettings()) {
            FilterDropDown(
                newsSites = state.newsSites,
                newsSitesSelected = state.newsSiteSelected,
                onDismissRequest = hideSettings,
                onNewsSiteChange = {
                    onEvent(PostEvent.SelectNewsSite(it))
                }
            )
        }
    }

}