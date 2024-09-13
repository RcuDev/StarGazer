package com.rcudev.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun getPlatformSize() = with(LocalConfiguration.current) {
    Size(
        screenWidthDp.toFloat(),
        screenHeightDp.toFloat()
    )
}

@Composable
actual fun getPlatformCachePath() = with(LocalContext.current) {
    val customCacheDir = File(cacheDir, IMAGE_CACHE_DIR)
    if (!customCacheDir.exists()) {
        customCacheDir.mkdirs()
    }
    customCacheDir.absolutePath
}