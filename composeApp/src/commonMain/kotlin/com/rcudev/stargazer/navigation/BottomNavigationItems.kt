package com.rcudev.stargazer.navigation

import kotlinx.serialization.Serializable

@Serializable
internal object Posts

@Serializable
internal data class WebView(val url: String)