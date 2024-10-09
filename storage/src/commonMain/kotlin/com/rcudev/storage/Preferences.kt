package com.rcudev.storage

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath

internal const val DATA_STORE_FILE_NAME = "star_gazer.preferences_pb"

// Preferences keys
val DARK_MODE = booleanPreferencesKey("dark_mode")
val POST_TYPE_FILTER = stringPreferencesKey("post_type_filter")
val NEWS_SITES_FILTER = stringPreferencesKey("news_sites_filter")

@Composable
internal expect fun dataStorePreferencesPath(): String

@Composable
fun dataStoragePreferences(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
): DataStore<Preferences> {
    val path = dataStorePreferencesPath()

    return PreferenceDataStoreFactory
        .createWithPath(
            scope = coroutineScope,
            produceFile = {
                path.toPath()
            }
        )
}