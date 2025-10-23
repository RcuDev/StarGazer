package com.rcudev.posts.ui.posts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@Composable
fun PostRoute(
    presenter: PostPresenter = koinInject(),
    showSettings: () -> Boolean,
    hideSettings: () -> Unit,
    showSnackBar: (String) -> Unit,
    onPostClick: (String) -> Unit,
    finishSplash: () -> Unit = {}
) {

    val state by presenter.state.collectAsStateWithLifecycle()

    PostScreen(
        presenter = presenter,
        state = state,
        onEvent = presenter::sendEvent,
        showSettings = showSettings,
        hideSettings = hideSettings,
        showSnackBar = showSnackBar,
        onPostClick = onPostClick,
        finishSplash = finishSplash
    )
}