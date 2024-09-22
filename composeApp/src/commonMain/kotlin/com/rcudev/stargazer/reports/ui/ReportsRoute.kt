package com.rcudev.stargazer.reports.ui

import androidx.compose.runtime.Composable
import com.rcudev.stargazer.common.ui.posts.PostScreen
import org.koin.compose.koinInject

@Composable
internal fun ReportsRoute(
    vm: ReportsViewModel = koinInject(),
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
        hideDropDown = hideDropDown
    )
}