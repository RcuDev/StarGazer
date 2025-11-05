package com.rcudev.posts.ui.settings

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.DARK_MODE
import com.rcudev.storage.NEWS_SITES_FILTER
import com.rcudev.storage.POST_TYPE_FILTER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPresenter(
    private val preferences: DataStore<Preferences>
) {

    @Composable
    fun present(events: Flow<SettingsEvent>): SettingsState {

        val postType by preferences.data
            .map { it[POST_TYPE_FILTER] ?: PostType.ARTICLES.type }
            .collectAsState(PostType.ARTICLES.type)

        val newsSitesFilter by preferences.data
            .map { it[NEWS_SITES_FILTER] ?: "" }
            .collectAsState("")

        val isDarkMode by preferences.data
            .map { it[DARK_MODE] ?: false }
            .collectAsState(false)

        val newsSites by remember { mutableStateOf(PostType.entries.map { it.type }) }

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is SettingsEvent.SelectPostType -> {
                        preferences.edit { prefs ->
                            prefs[POST_TYPE_FILTER] = event.postType
                        }
                    }
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
            postType = postType,
            newsSites = newsSites,
            newsSitesSelected = newsSitesFilter,
            isDarkMode = isDarkMode
        )
    }

}