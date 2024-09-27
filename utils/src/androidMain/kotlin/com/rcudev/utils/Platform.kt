package com.rcudev.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getPlatformSize() = with(LocalConfiguration.current) {
    Size(
        screenWidthDp.toFloat(),
        screenHeightDp.toFloat()
    )
}

@Composable
actual fun getPlatformCachePath(): String = with(LocalContext.current) {
    cacheDir.resolve(IMAGE_CACHE_DIR).absolutePath
}

actual fun logMessage(tag: String, message: String) {
    Log.d(tag, message)
}