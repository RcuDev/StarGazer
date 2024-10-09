package com.rcudev.stargazer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rcudev.posts.ui.posts.PostRoute
import com.rcudev.posts.ui.webview.WebViewRoute

@Composable
internal fun NavGraph(
    navController: NavHostController,
    showSnackBar: (String) -> Unit,
    showSettings: () -> Boolean,
    hideSettings: () -> Unit,
    finishSplash: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Posts
    ) {
        composable<Posts> {
            PostRoute(
                showSnackBar = showSnackBar,
                showSettings = showSettings,
                hideSettings = hideSettings,
                onPostClick = { url ->
                    navController.navigate(WebView(url = url))
                },
                finishSplash = finishSplash
            )
        }
        composable<WebView> { entry ->
            WebViewRoute(
                url = entry.toRoute<WebView>().url
            )
        }
    }
}