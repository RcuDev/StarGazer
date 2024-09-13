package com.rcudev.stargazer.reports.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rcudev.stargazer.common.ui.posts.PostScreen
import org.koin.compose.koinInject

@Composable
internal fun ReportsRoute(
    vm: ReportsViewModel = koinInject(),
    innerPadding: PaddingValues,
    onFilterClick: () -> Pair<Boolean, List<String>>,
    showSnackBar: (String) -> Unit,
    onReportClick: (String) -> Unit,
    hideDropDown: () -> Unit
) {
    PostScreen(
        vm = vm,
        onFilterClick = onFilterClick,
        showSnackBar = showSnackBar,
        onItemClick = onReportClick,
        hideDropDown = hideDropDown,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    )
}