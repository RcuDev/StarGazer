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
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.rcudev.ds.theme.Typography
import com.rcudev.storage.DARK_MODE
import com.rcudev.storage.NEWS_SITES_FILTER
import com.rcudev.utils.LocalScreenSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun FilterDropDown(
    preferences: DataStore<Preferences> = koinInject(),
    newsSites: List<String>,
    newsSitesSelected: String?,
    onDismissRequest: () -> Unit = {}
) {
    val screenSize = LocalScreenSize.current
    val scope = rememberCoroutineScope()
    val allNewsSites = remember { newsSites.toMutableStateList() }
    val selectedNewsSites =
        remember { (newsSitesSelected?.split(",") ?: listOf()).toMutableStateList() }
    val orderedItems = remember {
        allNewsSites
            .sortedByDescending { it in selectedNewsSites }
            .filter { it.isNotEmpty() }
            .distinct()
    }

    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = { Text("Settings") },
        text = {
            Column {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                DarkModeItem(
                    preferences = preferences,
                    scope = scope
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 16.dp)
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
                scope.launch {
                    preferences.edit {
                        it[NEWS_SITES_FILTER] = selectedNewsSites.joinToString(",")
                    }
                }
                onDismissRequest()
            }) {
                Text("Confirm")
            }
        },
        modifier = Modifier
            .heightIn(max = (screenSize.height * 0.7f).dp)
    )
}

@Composable
private fun DarkModeItem(
    preferences: DataStore<Preferences> = koinInject(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val isChecked = preferences.data.map { prefs -> prefs[DARK_MODE] }
        .collectAsState(initial = false)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Dark mode",
            style = Typography.titleMedium,
            modifier = Modifier
                .weight(1f)
        )
        Switch(
            checked = isChecked.value == true,
            onCheckedChange = {
                scope.launch {
                    preferences.edit {
                        it[DARK_MODE] = isChecked.value != true
                    }
                }
            },
            modifier = Modifier
                .scale(0.75f)
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