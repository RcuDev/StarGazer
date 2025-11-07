package com.rcudev.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import okio.Path.Companion.toPath

// Composition locals
val LocalScreenSize = compositionLocalOf<Size> { error("No screen size provided") }
val LocalImageLoader = compositionLocalOf<ImageLoader> { error("No image loader provided") }

@Composable
fun rememberImageLoader(): ImageLoader {
    val context = LocalPlatformContext.current
    val cachePath = with(context) { getPlatformCachePath().toPath() }

    return remember(context) {
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context = context, percent = 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cachePath)
                    .maxSizePercent(0.5)
                    .build()
            }
            .logger(DebugLogger())
            .crossfade(300)
            .build()
    }
}