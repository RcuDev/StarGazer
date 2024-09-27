package com.rcudev.storage

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun dataStorePreferencesPath(): String = with(LocalContext.current) {
    filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
}
