package com.rcudev.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath

internal const val DATA_STORE_FILE_NAME = "star_gazer.preferences_pb"

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