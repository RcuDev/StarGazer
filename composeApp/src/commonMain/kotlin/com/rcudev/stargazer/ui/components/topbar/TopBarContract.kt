package com.rcudev.stargazer.ui.components.topbar

sealed interface TopBarState {
    data object Idle : TopBarState
    data class Content(
        val selectedPostType: String
    ) : TopBarState
}

sealed interface TopBarEvent {
    data class SelectPostType(val postType: String) : TopBarEvent
}
