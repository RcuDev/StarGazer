package com.rcudev.posts.ui.posts

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject

@Composable
fun PostRoute(
    vm: PostViewModel = koinInject(),
    showSnackBar: (String) -> Unit,
    onPostClick: (String) -> Unit,
    finishSplash: () -> Unit = {}
) {
    PostScreen(
        vm = vm,
        showSnackBar = showSnackBar,
        onPostClick = onPostClick,
        finishSplash = finishSplash
    )
}