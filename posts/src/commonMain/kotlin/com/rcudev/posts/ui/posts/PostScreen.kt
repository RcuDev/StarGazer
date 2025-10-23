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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PostContent(
            state = state,
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