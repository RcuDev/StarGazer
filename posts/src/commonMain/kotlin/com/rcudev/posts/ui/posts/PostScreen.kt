package com.rcudev.posts.ui.posts

import androidx.compose.runtime.*

@Composable
internal fun PostScreen(
    vm: PostViewModel,
    showSnackBar: (String) -> Unit,
    onPostClick: (String) -> Unit,
) {

    val viewState by vm.state.collectAsState()

    PostContent(
        viewState = viewState,
        loadNextPage = vm::loadNextPage,
        showSnackBar = {
            showSnackBar("Loading more post")
        },
        onItemClick = onPostClick
    )
}