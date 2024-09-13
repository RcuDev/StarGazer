package com.rcudev.stargazer.webview.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

@Composable
internal expect fun WebViewRoute(
    innerPadding: PaddingValues,
    url: String
)