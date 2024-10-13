package com.rcudev.posts.ui.webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
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

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun WebViewRoute(
    url: String,
    notAuthorizedHost: () -> Unit
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
                this.navigationDelegate = SGWebViewDelegate(Url(url), notAuthorizedHost)
                this.loadRequest(NSURLRequest(NSURL(string = url)))
            }
        },
        update = { webView ->
            if (webView.URL?.absoluteString != url) {
                webView.loadRequest(NSURLRequest(NSURL(string = url)))
            }
        },
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative,
            isNativeAccessibilityEnabled = true,
        ),
        modifier = Modifier.fillMaxSize()
    )
}

class SGWebViewDelegate(private val url: Url, val notAuthorizedHost: () -> Unit) : NSObject(), WKNavigationDelegateProtocol {
    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit
    ) {
        val navigationType = decidePolicyForNavigationAction.navigationType
        val webUrl = Url(decidePolicyForNavigationAction.request.URL?.absoluteString.orEmpty())
        when(navigationType){
            WKNavigationTypeLinkActivated -> {
                if (webUrl.host != url.host) {
                    decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                } else {
                    notAuthorizedHost()
                    decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                }
            }
            else -> decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
        }
    }
}
