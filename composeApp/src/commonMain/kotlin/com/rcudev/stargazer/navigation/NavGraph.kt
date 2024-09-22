package com.rcudev.stargazer.navigation

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
                onFilterClick = onFilterClick,
                showSnackBar = showSnackBar,
                onArticleClick = openWebView,
                hideDropDown = hideDropDown
            )
        }
        composable<Blogs> {
            BlogsRoute(
                onFilterClick = onFilterClick,
                showSnackBar = showSnackBar,
                onBlogClick = openWebView,
                hideDropDown = hideDropDown
            )
        }
        composable<Reports> {
            ReportsRoute(
                onFilterClick = onFilterClick,
                showSnackBar = showSnackBar,
                onReportClick = openWebView,
                hideDropDown = hideDropDown
            )
        }
        composable<WebView> { entry ->
            WebViewRoute(
                url = entry.toRoute<WebView>().url
            )
        }
    }
}