package com.rcudev.posts.ui.webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import io.ktor.http.Url
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.*
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.javaScriptEnabled
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun WebViewRoute(
    url: String
) {
    if (url.isEmpty()) {
        EmptyWebView()
        return
    }

    UIKitView(
        factory = {
            val configuration = WKWebViewConfiguration().apply {
                preferences.apply {
                    javaScriptEnabled = true
                }
                allowsInlineMediaPlayback = true
            }
            WKWebView(frame = CGRectZero.readValue(), configuration = configuration).apply {
                this.navigationDelegate = SGWebViewDelegate(Url(url))
                this.loadRequest(NSURLRequest(NSURL(string = url)))
            }
        },
        update = { webView ->
            if (webView.URL?.absoluteString != url) {
                webView.loadRequest(NSURLRequest(NSURL(string = url)))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

class SGWebViewDelegate(private val url: Url) : NSObject(), WKNavigationDelegateProtocol {
    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit
    ) {
        val webUrl = Url(decidePolicyForNavigationAction.request.URL?.absoluteString ?: "")
        if (webUrl.host != url.host) {
            decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
        } else {
            decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
        }
    }

    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
        // Handle what to do when navigation finishes if needed
    }
}
