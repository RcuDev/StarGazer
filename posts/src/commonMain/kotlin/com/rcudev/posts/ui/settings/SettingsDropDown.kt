package com.rcudev.posts.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.rcudev.ds.theme.Typography
import com.rcudev.utils.LocalScreenSize
import org.koin.compose.koinInject

@Composable
fun SettingsDropDown(
    vm: SettingsViewModel = koinInject(),
    onDismissRequest: () -> Unit = {}
) {
    val state by vm.state.collectAsState()
    val screenSize = LocalScreenSize.current

    val selectedNewsSites = remember(state) {
        when (state) {
            is SettingsState.Content -> {
                val selected = (state as SettingsState.Content).newsSitesSelected
                    .split(",")
                    .filter { it.isNotEmpty() }
                selected.toMutableStateList()
            }
            else -> mutableStateListOf()
        }
    }

    when (val currentState = state) {
        is SettingsState.Loading -> {
            AlertDialog(
                onDismissRequest = onDismissRequest,
                title = { Text("Settings") },
                text = { Text("Loading settings...") },
                confirmButton = {}
            )
        }

        is SettingsState.Content -> {
            val orderedItems = remember(currentState.newsSites, selectedNewsSites) {
                currentState.newsSites
                    .sortedByDescending { it in selectedNewsSites }
                    .filter { it.isNotEmpty() }
                    .distinct()
            }

            AlertDialog(
                onDismissRequest = onDismissRequest,
                title = { Text("Settings") },
                text = {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        DarkModeItem(vm = vm)

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        Text(
                            text = "News sites",
                            style = Typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = orderedItems,
                                key = { it }
                            ) { newsSite ->
                                NewsSiteItem(
                                    newsSite = newsSite,
                                    isSelected = newsSite in selectedNewsSites,
                                    onCheckChange = { isChecked ->
                                        if (isChecked) {
                                            selectedNewsSites.add(newsSite)
                                        } else {
                                            selectedNewsSites.remove(newsSite)
                                        }
                                    }
                                )
                            }
                        }

                        HorizontalDivider()
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        vm.selectNewsSite(selectedNewsSites.joinToString(","))
                        onDismissRequest()
                    }) {
                        Text("Confirm")
                    }
                },
                modifier = Modifier.heightIn(max = (screenSize.height * 0.7f).dp)
            )
        }
    }
}

@Composable
fun DarkModeItem(
    vm: SettingsViewModel
) {
    val state by vm.state.collectAsState()

    val isDarkMode = when (state) {
        is SettingsState.Content -> (state as SettingsState.Content).isDarkMode
        else -> false
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Dark mode",
            style = Typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isDarkMode,
            onCheckedChange = { isChecked ->
                vm.toggleDarkMode(isChecked)
            },
            modifier = Modifier
                .scale(0.75f)
                .testTag("DarkModeSwitch")
        )
    }
}

@Composable
private fun NewsSiteItem(
    newsSite: String,
    isSelected: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onCheckChange
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(newsSite)
    }
}
