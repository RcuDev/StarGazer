@file:OptIn(ExperimentalLayoutApi::class)

package com.rcudev.stargazer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.rcudev.ds.theme.Typography
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private val ARTICLE_KEY = booleanPreferencesKey("articlesSelected")
private val BlOGS_KEY = booleanPreferencesKey("blogsSelected")
private val REPORTS_KEY = booleanPreferencesKey("reportsSelected")

@Composable
internal fun TopBar(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .heightIn(min = 64.dp)
    ) {
        if (showBackButton) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable {
                        onBackClick()
                    }
            )
        }

        Text(
            text = "StarGazer",
            style = Typography.titleLarge
        )

        if (!showBackButton) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
            ) {
                FilterChip(
                    text = "Articles",
                    preferenceKey = ARTICLE_KEY
                )
                FilterChip(
                    text = "Blogs",
                    preferenceKey = BlOGS_KEY
                )
                FilterChip(
                    text = "Reports",
                    preferenceKey = REPORTS_KEY
                )
            }

        }
    }
}

@Composable
private fun FlowRowScope.FilterChip(
    preferences: DataStore<Preferences> = koinInject(),
    text: String,
    preferenceKey: Preferences.Key<Boolean>
) {
    val scope = rememberCoroutineScope()
    val isSelected = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isSelected.value = preferences.data.first()[preferenceKey] == true
    }

    FilterChip(
        label = {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        selected = isSelected.value,
        onClick = {
            scope.launch {
                preferences.edit {
                    isSelected.value = !isSelected.value
                    it[preferenceKey] = isSelected.value
                }
            }
        },
        modifier = Modifier
            .weight(1f)
    )
}