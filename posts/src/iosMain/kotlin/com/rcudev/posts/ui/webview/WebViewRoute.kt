package com.rcudev.posts.ui.webview

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKNavigationTypeLinkActivated
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
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

    val nsUrl = remember { NSURL(string = url) }
    val navigationDelegate = remember { CustomNavigationDelegate(nsUrl) }

    UIKitView(
        factory = {
            WKWebView().apply {
                WKWebViewConfiguration().apply {
                    allowsInlineMediaPlayback = true
                    defaultWebpagePreferences.allowsContentJavaScript = true
                }
                this.navigationDelegate = navigationDelegate
                scrollView.setScrollEnabled(true)
                loadRequest(NSURLRequest(nsUrl))
            }
        },
        update = { webView ->
            webView.apply {
                loadRequest(NSURLRequest(nsUrl))
            }
        },
        onRelease = {
            it.navigationDelegate = null
        },
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true,
        ),
        modifier = Modifier.Companion
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                }
            }
    )
}

private class CustomNavigationDelegate(val originalUrl: NSURL) : NSObject(),
    WKNavigationDelegateProtocol {
    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit

    ) {
        val type = decidePolicyForNavigationAction.navigationType
        val host = decidePolicyForNavigationAction.request.URL?.host
        when (type) {
            WKNavigationTypeLinkActivated -> decisionHandler(
                if (host != originalUrl.host) WKNavigationActionPolicy.WKNavigationActionPolicyCancel
                else WKNavigationActionPolicy.WKNavigationActionPolicyAllow
            )

            else -> decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
        }
    }
}