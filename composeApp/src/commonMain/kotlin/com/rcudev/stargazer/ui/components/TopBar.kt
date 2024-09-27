package com.rcudev.stargazer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rcudev.ds.theme.Typography

@OptIn(ExperimentalLayoutApi::class)
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
                    label = {
                        Text(
                            text = "Articles",
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    selected = true,
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                )
                FilterChip(
                    label = {
                        Text(
                            text = "Blogs",
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    selected = false,
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                )
                FilterChip(
                    label = {
                        Text(
                            text = "Reports",
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    selected = false,
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                )
            }

        }
    }

}