package com.rcudev.stargazer.navigation

import kotlinx.serialization.Serializable

internal val bottomNavDestinations = listOf<NavigationDestination>(Articles, Blogs, Reports, WebView(""))

@Serializable
internal sealed class NavigationDestination(
    val name: String,
)

@Serializable
internal object Articles : NavigationDestination("Articles")

@Serializable
internal object Blogs : NavigationDestination("Blogs")

@Serializable
internal object Reports : NavigationDestination("Reports")

@Serializable
internal data class WebView(val url: String) : NavigationDestination("WebView")