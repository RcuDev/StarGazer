package com.rcudev.posts.ui.settings

// Immutable settings state
sealed interface SettingsState {
    data object Loading : SettingsState

    data class Content(
        val postType: String,
        val newsSites: List<String>,
        val newsSitesSelected: String,
        val isDarkMode: Boolean
    ) : SettingsState
}

// Settings events
sealed interface SettingsEvent {
    data class SelectPostType(val postType: String) : SettingsEvent
    data class SelectNewsSite(val newsSite: String) : SettingsEvent
    data class ToggleDarkMode(val isEnabled: Boolean) : SettingsEvent
}