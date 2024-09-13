package com.rcudev.stargazer.articles.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rcudev.stargazer.common.ui.posts.PostScreen
import org.koin.compose.koinInject

@Composable
internal fun ArticlesRoute(
    vm: ArticlesViewModel = koinInject(),
    innerPadding: PaddingValues,
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
        hideDropDown = hideDropDown,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    )
}