@file:OptIn(ExperimentalLayoutApi::class)

package com.rcudev.stargazer.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.rcudev.ds.theme.Typography
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.POST_TYPE_FILTER
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun TopBar(
    preferences: DataStore<Preferences> = koinInject(),
    showBackButton: Boolean,
    onBackClick: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(
                elevation = 6.dp,
                spotColor = MaterialTheme.colorScheme.onSurface
            )
            .background(MaterialTheme.colorScheme.secondaryContainer)
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
                    .testTag("Back")
            )
        }

        Text(
            text = "Star Gazer",
            style = Typography.titleLarge
        )

        if (!showBackButton) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("PostTypeChips")
            ) {
                FilterChip(
                    preferences = preferences,
                    text = PostType.ARTICLES.type
                )
                FilterChip(
                    preferences = preferences,
                    text = PostType.BLOGS.type
                )
                FilterChip(
                    preferences = preferences,
                    text = PostType.REPORTS.type
                )
            }

        }
    }
}

@Composable
private fun FlowRowScope.FilterChip(
    preferences: DataStore<Preferences>,
    text: String,
) {
    val scope = rememberCoroutineScope()
    val isSelected = preferences.data.map { prefs -> prefs[POST_TYPE_FILTER] }
        .collectAsState(initial = PostType.ARTICLES.type)

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
        colors = FilterChipDefaults.filterChipColors().copy(
            selectedContainerColor = MaterialTheme.colorScheme.inversePrimary
        ),
        selected = isSelected.value == text,
        onClick = {
            scope.launch {
                preferences.edit {
                    it[POST_TYPE_FILTER] = text
                }
            }
        },
        modifier = Modifier
            .weight(1f)
            .testTag(text)
    )
}