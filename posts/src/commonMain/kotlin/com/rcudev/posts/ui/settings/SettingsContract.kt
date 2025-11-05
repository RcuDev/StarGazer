package com.rcudev.posts.ui.settings

// Immutable settings state
sealed interface SettingsState {
    data object Loading : SettingsState

    data class Content(
        val newsSites: List<String>,
        val newsSitesSelected: String,
        val isDarkMode: Boolean
    ) : SettingsState
}

// Settings events
sealed interface SettingsEvent {
    data class SelectNewsSite(val newsSite: String) : SettingsEvent
    data class ToggleDarkMode(val isEnabled: Boolean) : SettingsEvent
}