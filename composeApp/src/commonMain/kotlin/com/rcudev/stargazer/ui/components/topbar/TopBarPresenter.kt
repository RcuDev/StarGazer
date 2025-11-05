package com.rcudev.stargazer.ui.components.topbar

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.POST_TYPE_FILTER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TopBarPresenter(
    private val preferences: DataStore<Preferences>
) {

    @Composable
    fun present(events: Flow<TopBarEvent>): TopBarState {

        val selectedPostType by preferences.data
            .map { it[POST_TYPE_FILTER] ?: PostType.ARTICLES.type }
            .collectAsState(PostType.ARTICLES.type)

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is TopBarEvent.SelectPostType -> {
                        preferences.edit { prefs ->
                            prefs[POST_TYPE_FILTER] = event.postType
                        }
                    }
                }
            }
        }

        return TopBarState.Content(
            selectedPostType = selectedPostType
        )
    }
}
