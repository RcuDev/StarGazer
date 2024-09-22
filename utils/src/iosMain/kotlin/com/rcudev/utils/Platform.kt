package com.rcudev.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getPlatformSize() = with(LocalDensity.current) {
    Size(
        LocalWindowInfo.current.containerSize.width.toDp().value,
        LocalWindowInfo.current.containerSize.height.toDp().value
    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun getPlatformCachePath(): String {
    val cacheDirectories = NSSearchPathForDirectoriesInDomains(
        directory = NSCachesDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true
    )
    val cacheDir = cacheDirectories.firstOrNull() as? String ?: NSTemporaryDirectory()
    val customCacheDir = "$cacheDir/${IMAGE_CACHE_DIR}"
    val fileManager = NSFileManager.defaultManager

    if (!fileManager.fileExistsAtPath(customCacheDir)) {
        fileManager.createDirectoryAtPath(
            customCacheDir,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )
    }

    return customCacheDir
}

actual fun logMessage(tag: String, message: String) {
    NSLog("$tag: $message")
}