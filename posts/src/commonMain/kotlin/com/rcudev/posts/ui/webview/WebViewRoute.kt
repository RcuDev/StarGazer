package com.rcudev.posts.ui.webview

import androidx.compose.runtime.Composable

@Composable
expect fun WebViewRoute(
    url: String,
    notAuthorizedHost: () -> Unit
)