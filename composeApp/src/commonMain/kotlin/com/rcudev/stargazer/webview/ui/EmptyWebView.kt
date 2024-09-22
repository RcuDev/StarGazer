package com.rcudev.stargazer.webview.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
internal fun EmptyWebView() {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Select an article, blog or report to view it.",
            textAlign = TextAlign.Center
        )
    }
}