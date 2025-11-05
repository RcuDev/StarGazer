package com.rcudev.posts.ui.posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rcudev.posts.ui.ViewState
import com.rcudev.posts.ui.posts.components.PostsEmpty
import com.rcudev.posts.ui.posts.components.PostsError
import com.rcudev.posts.ui.posts.components.PostsLoading
import com.rcudev.posts.ui.settings.FilterDropDown
import com.rcudev.posts.ui.settings.SettingsDropDown

@Composable
internal fun PostScreen(
    vm: PostViewModel,
    showSettings: () -> Boolean,
    hideSettings: () -> Unit,
    showSnackBar: (String) -> Unit,
    onPostClick: (String) -> Unit,
    finishSplash: () -> Unit = {}
) {

    val state  by vm.state.collectAsState()

    LaunchedEffect(state) {
        if (state !is PostState.Loading) {
            finishSplash()
        }
    }

    LaunchedEffect(state) {
        if (state is PostState.Content && (state as PostState.Content).showLoadPageError) {
            showSnackBar("Error loading more posts")
            vm.dismissLoadPageError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (val currentState = state) {
            is PostState.Content -> {
                PostsContent(
                    posts = currentState.posts,
                    loadingNextPage = currentState.loadingNextPage,
                    onLoadNextPage = vm::loadNextPage,
                    onItemClick = onPostClick
                )

                if (showSettings()) {
                    SettingsDropDown(
                        onDismissRequest = hideSettings
                    )
                }

            }
            is PostState.Empty -> PostsEmpty(message = currentState.message)
            is PostState.Error -> PostsError(onRetry = vm::retry)
            is PostState.Loading -> PostsLoading()
        }
    }

}