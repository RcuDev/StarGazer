package com.rcudev.stargazer.webview.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun WebViewRoute(
    innerPadding: PaddingValues,
    url: String
) {

    if (url.isEmpty()) {
        EmptyWebView(innerPadding)
        return
    }

    val nsUrl = remember { NSURL(string = url) }
    val navigationDelegate = remember { CustomNavigationDelegate(nsUrl) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        UIKitView(
            factory = {
                WKWebView().apply {
                    this.navigationDelegate = navigationDelegate
                    scrollView.scrollEnabled = true
                    setAutoresizingMask(UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight)
                    loadRequest(NSURLRequest(nsUrl))
                }
            },
            update = { webView ->
                webView.apply {
                    loadRequest(NSURLRequest(nsUrl))
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private class CustomNavigationDelegate(val originalUrl: NSURL) : NSObject(),
    WKNavigationDelegateProtocol {
    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit

    ) {
        val host = decidePolicyForNavigationAction.request.URL?.host
        decisionHandler(
            if (host != originalUrl.host) WKNavigationActionPolicy.WKNavigationActionPolicyCancel
            else WKNavigationActionPolicy.WKNavigationActionPolicyAllow
        )
    }
}