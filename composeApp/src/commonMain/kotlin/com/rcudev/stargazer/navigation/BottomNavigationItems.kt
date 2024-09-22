package com.rcudev.stargazer.navigation

import kotlinx.serialization.Serializable

internal val bottomNavDestinations = listOf<NavigationDestination>(Posts, WebView(""))

@Serializable
internal sealed class NavigationDestination(
    val name: String,
)

@Serializable
internal object Posts : NavigationDestination("Posts")

@Serializable
internal data class WebView(val url: String) : NavigationDestination("WebView")