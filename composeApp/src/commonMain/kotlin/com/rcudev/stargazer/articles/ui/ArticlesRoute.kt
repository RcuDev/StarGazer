package com.rcudev.stargazer.articles.ui

import androidx.compose.runtime.Composable
import com.rcudev.stargazer.common.ui.posts.PostScreen
import org.koin.compose.koinInject

@Composable
internal fun ArticlesRoute(
    vm: ArticlesViewModel = koinInject(),
    onFilterClick: () -> Pair<Boolean, List<String>>,
    showSnackBar: (String) -> Unit,
    onArticleClick: (String) -> Unit,
    hideDropDown: () -> Unit
) {
    PostScreen(
        vm = vm,
        onFilterClick = onFilterClick,
        showSnackBar = showSnackBar,
        onItemClick = onArticleClick,
        hideDropDown = hideDropDown
    )
}