@file:OptIn(ExperimentalLayoutApi::class)

package com.rcudev.stargazer.ui.components.topbar

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rcudev.ds.theme.Typography
import com.rcudev.posts.domain.model.PostType
import org.koin.compose.koinInject

@Composable
internal fun TopBar(
    vm: TopBarViewModel = koinInject(),
    showBackButton: Boolean,
    onBackClick: () -> Unit = {},
) {
    val state by vm.state.collectAsState()

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

        if (!showBackButton && state is TopBarState.Content) {
            val contentState = state as TopBarState.Content

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("PostTypeChips")
            ) {
                FilterChip(
                    text = PostType.ARTICLES.type,
                    isSelected = contentState.selectedPostType == PostType.ARTICLES.type,
                    onClick = { vm.selectPostType(PostType.ARTICLES.type) }
                )
                FilterChip(
                    text = PostType.BLOGS.type,
                    isSelected = contentState.selectedPostType == PostType.BLOGS.type,
                    onClick = { vm.selectPostType(PostType.BLOGS.type) }
                )
                FilterChip(
                    text = PostType.REPORTS.type,
                    isSelected = contentState.selectedPostType == PostType.REPORTS.type,
                    onClick = { vm.selectPostType(PostType.REPORTS.type) }
                )
            }
        }
    }
}

@Composable
private fun FlowRowScope.FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        label = {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        colors = FilterChipDefaults.filterChipColors().copy(
            selectedContainerColor = MaterialTheme.colorScheme.inversePrimary
        ),
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .testTag(text)
    )
}
