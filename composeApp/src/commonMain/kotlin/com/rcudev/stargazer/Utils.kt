package com.rcudev.stargazer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Size
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.disk.DiskCache
import coil3.request.crossfade
import com.rcudev.utils.getPlatformCachePath
import okio.Path.Companion.toPath

// Composition locals
internal val LocalScreenSize = compositionLocalOf<Size> { error("No screen size provided") }
internal val LocalImageLoader =
    compositionLocalOf<ImageLoader> { error("No image loader provided") }

@Composable
internal fun GetImageLoader() = with(LocalPlatformContext.current) {
    ImageLoader.Builder(this)
        .diskCache(
            DiskCache.Builder()
                .directory(getPlatformCachePath().toPath())
                .maxSizePercent(0.25)
                .build()
        )
        .crossfade(true)
        .build()
}