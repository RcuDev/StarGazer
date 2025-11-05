package com.rcudev.stargazer.ui.components.topbar

sealed interface TopBarState {
    data class Content(
        val selectedPostType: String
    ) : TopBarState
}

sealed interface TopBarEvent {
    data class SelectPostType(val postType: String) : TopBarEvent
}
