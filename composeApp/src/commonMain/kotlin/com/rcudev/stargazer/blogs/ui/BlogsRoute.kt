package com.rcudev.stargazer.blogs.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rcudev.stargazer.common.ui.posts.PostScreen
import org.koin.compose.koinInject

@Composable
internal fun BlogsRoute(
    vm: BlogsViewModel = koinInject(),
    innerPadding: PaddingValues,
    onFilterClick: () -> Pair<Boolean, List<String>>,
    showSnackBar: (String) -> Unit,
    onBlogClick: (String) -> Unit,
    hideDropDown: () -> Unit
) {
    PostScreen(
        vm = vm,
        onFilterClick = onFilterClick,
        showSnackBar = showSnackBar,
        onItemClick = onBlogClick,
        hideDropDown = hideDropDown,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    )
}