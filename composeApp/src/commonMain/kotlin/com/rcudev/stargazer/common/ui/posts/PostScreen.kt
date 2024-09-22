package com.rcudev.stargazer.common.ui.posts

import androidx.compose.runtime.*
import com.rcudev.stargazer.common.ui.filters.FilterDropDown

@Composable
internal fun PostScreen(
    vm: BaseViewModel,
    onFilterClick: () -> Pair<Boolean, List<String>>,
    showSnackBar: (String) -> Unit,
    onItemClick: (String) -> Unit,
    hideDropDown: () -> Unit
) {

    val viewState by vm.state.collectAsState()
    val selectedNewsSites by vm.newsSites.collectAsState("")
    val (showFilters, newsSites) = onFilterClick()

    PostContent(
        viewState = viewState,
        loadNextPage = vm::loadNextPage,
        showSnackBar = {
            showSnackBar("Loading more post")
        },
        onItemClick = onItemClick
    )

    if (showFilters) {
        FilterDropDown(
            newsSites = newsSites,
            newsSitesSelected = selectedNewsSites,
            onDismissRequest = { newsSites ->
                hideDropDown()
                vm.newsSites.value = newsSites.joinToString(",")
            }
        )
    }
}