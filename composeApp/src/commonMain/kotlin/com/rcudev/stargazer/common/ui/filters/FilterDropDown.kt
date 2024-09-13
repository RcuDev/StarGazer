package com.rcudev.stargazer.common.ui.filters

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rcudev.stargazer.LocalScreenSize

@Composable
internal fun FilterDropDown(
    newsSites: List<String>,
    newsSitesSelected: String?,
    onDismissRequest: (selectedNewsSites: List<String>) -> Unit = {}
) {
    val screenSize = LocalScreenSize.current
    val allNewsSites = remember { newsSites.toMutableStateList() }
    val selectedNewsSites =
        remember { (newsSitesSelected?.split(",") ?: listOf()).toMutableStateList() }

    // Derived state to order the list
    val orderedNewsSites = remember {
        val result = mutableListOf<String>()
        result.addAll(selectedNewsSites)
        result.addAll(allNewsSites.filterNot { it in selectedNewsSites })
        result
    }

    AlertDialog(
        onDismissRequest = {
            onDismissRequest(newsSitesSelected?.split(",") ?: listOf())
        },
        title = { Text("Filter News Site") },
        text = {
            LazyColumn {
                items(orderedNewsSites.filter { it.isNotEmpty() }) { newsSite ->
                    FilterItem(
                        newsSite = newsSite,
                        isSelected = newsSite in selectedNewsSites,
                        onCheckChange = { isChecked ->
                            if (isChecked) {
                                allNewsSites.remove(newsSite)
                                selectedNewsSites.add(newsSite)
                            } else {
                                selectedNewsSites.remove(newsSite)
                                allNewsSites.add(newsSite)
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismissRequest(selectedNewsSites.filter { it.isNotEmpty() })
            }) {
                Text("Confirm")
            }
        },
        modifier = Modifier
            .heightIn(max = (screenSize.height / 2).dp)
    )
}

@Composable
private fun FilterItem(
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