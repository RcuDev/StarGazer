package com.rcudev.posts.ui.settings

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.rcudev.posts.domain.InfoService
import com.rcudev.storage.DARK_MODE
import com.rcudev.storage.NEWS_SITES_FILTER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPresenter(
    private val preferences: DataStore<Preferences>,
    private val infoService: InfoService
) {

    @Composable
    fun present(events: Flow<SettingsEvent>): SettingsState {

        val newsSitesFilter by preferences.data
            .map { it[NEWS_SITES_FILTER] ?: "" }
            .collectAsState("")

        val isDarkMode by preferences.data
            .map { it[DARK_MODE] ?: false }
            .collectAsState(false)

        var newsSites by remember { mutableStateOf(emptyList<String>()) }

        LaunchedEffect(Unit) {
            infoService.getInfo().fold(
                onSuccess = { data -> newsSites = data.newsSites },
                onFailure = { /* No-op */ }
            )
        }

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is SettingsEvent.SelectNewsSite -> {
                        preferences.edit { prefs ->
                            prefs[NEWS_SITES_FILTER] = event.newsSite
                        }
                    }
                    is SettingsEvent.ToggleDarkMode -> {
                        preferences.edit { prefs ->
                            prefs[DARK_MODE] = event.isEnabled
                        }
                    }
                }
            }
        }

        return SettingsState.Content(
            newsSites = newsSites,
            newsSitesSelected = newsSitesFilter,
            isDarkMode = isDarkMode
        )
    }

}