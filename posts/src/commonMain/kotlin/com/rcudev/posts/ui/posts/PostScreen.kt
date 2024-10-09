package com.rcudev.posts.ui.posts

import androidx.compose.runtime.*
import com.rcudev.posts.ui.ViewState

@Composable
internal fun PostScreen(
    vm: PostViewModel,
    showSnackBar: (String) -> Unit,
    onPostClick: (String) -> Unit,
    finishSplash: () -> Unit = {}
) {

    val viewState by vm.state.collectAsState()

    LaunchedEffect(viewState) {
        if (viewState !is ViewState.Loading) {
            finishSplash()
        }
    }

    PostContent(
        viewState = viewState,
        loadNextPage = vm::loadNextPage,
        showSnackBar = {
            showSnackBar("Loading more post")
        },
        onItemClick = onPostClick
    )
}