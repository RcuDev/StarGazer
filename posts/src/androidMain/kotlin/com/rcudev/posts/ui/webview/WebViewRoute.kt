package com.rcudev.posts.ui.webview

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.ktor.http.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun WebViewRoute(
    url: String,
    notAuthorizedHost: () -> Unit
) {
    if (url.isEmpty()) {
        EmptyWebView()
        return
    }

    AndroidView(
        factory = {
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
                webViewClient = SGWebViewClient(Url(url), notAuthorizedHost)
            }
        },
        update = {
            it.loadUrl(url)
        },
        modifier = Modifier
            .fillMaxSize()
    )
}

private class SGWebViewClient(val url: Url, val notAuthorizedHost: () -> Unit) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val webUrl = Url(request?.url.toString())
        if (webUrl.host != url.host) notAuthorizedHost()
        return webUrl.host != url.host
    }
}