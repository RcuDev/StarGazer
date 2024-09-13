package com.rcudev.stargazer.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rcudev.stargazer.articles.ui.ArticlesRoute
import com.rcudev.stargazer.blogs.ui.BlogsRoute
import com.rcudev.stargazer.reports.ui.ReportsRoute
import com.rcudev.stargazer.webview.ui.WebViewRoute

@Composable
internal fun NavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    showSnackBar: (String) -> Unit,
    openWebView: (String) -> Unit,
    onFilterClick: () -> Pair<Boolean, List<String>>,
    hideDropDown: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Articles
    ) {
        composable<Articles> {
            ArticlesRoute(
                innerPadding = innerPadding,
                onFilterClick = onFilterClick,
                showSnackBar = showSnackBar,
                onArticleClick = openWebView,
                hideDropDown = hideDropDown
            )
        }
        composable<Blogs> {
            BlogsRoute(
                innerPadding = innerPadding,
                onFilterClick = onFilterClick,
                showSnackBar = showSnackBar,
                onBlogClick = openWebView,
                hideDropDown = hideDropDown
            )
        }
        composable<Reports> {
            ReportsRoute(
                innerPadding = innerPadding,
                onFilterClick = onFilterClick,
                showSnackBar = showSnackBar,
                onReportClick = openWebView,
                hideDropDown = hideDropDown
            )
        }
        composable<WebView> { entry ->
            WebViewRoute(
                innerPadding = innerPadding,
                url = entry.toRoute<WebView>().url
            )
        }
    }
}