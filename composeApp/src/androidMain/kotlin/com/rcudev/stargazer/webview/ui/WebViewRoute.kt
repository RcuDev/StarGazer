package com.rcudev.stargazer.webview.ui

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.ktor.http.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal actual fun WebViewRoute(
    innerPadding: PaddingValues,
    url: String
) {
    if (url.isEmpty()) {
        EmptyWebView(innerPadding)
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        AndroidView(factory = {
            WebView(it).apply {
                scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                }
                webViewClient = SGWebViewClient(Url(url))
            }
        }, update = {
            it.loadUrl(url)
        })
    }
}

private class SGWebViewClient(val url: Url) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val webUrl = Url(request?.url.toString())
        return webUrl.host != url.host
    }
}