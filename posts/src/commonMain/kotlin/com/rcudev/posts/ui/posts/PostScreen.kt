package com.rcudev.posts.ui.posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rcudev.posts.ui.posts.ViewState
import com.rcudev.posts.ui.settings.FilterDropDown

@Composable
internal fun PostScreen(
    vm: PostViewModel,
    showSettings: () -> Boolean,
    hideSettings: () -> Unit,
    showSnackBar: (String) -> Unit,
    onPostClick: (String) -> Unit,
    finishSplash: () -> Unit = {}
) {

    val viewState by vm.state.collectAsState()
    val newsSites by vm.newsSites.collectAsState()

    LaunchedEffect(viewState) {
        if (viewState !is ViewState.Loading) {
            finishSplash()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PostContent(
            viewState = viewState,
            loadNextPage = vm::loadNextPage,
            showSnackBar = {
                showSnackBar("Loading more post")
            },
            onItemClick = onPostClick
        )

        if (showSettings()) {
            FilterDropDown(
                newsSites = newsSites,
                newsSitesSelected = vm.newsSitesSelected.value,
                onDismissRequest = hideSettings
            )
        }
    }

}