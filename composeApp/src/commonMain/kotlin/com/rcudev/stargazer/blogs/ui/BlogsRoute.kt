package com.rcudev.stargazer.blogs.ui

import androidx.compose.runtime.Composable
import com.rcudev.stargazer.common.ui.posts.PostScreen
import org.koin.compose.koinInject

@Composable
internal fun BlogsRoute(
    vm: BlogsViewModel = koinInject(),
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
    )
}