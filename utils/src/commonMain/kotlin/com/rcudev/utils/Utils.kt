package com.rcudev.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size

internal const val IMAGE_CACHE_DIR = "image_cache"

// Platform specific functions
@Composable
expect fun getPlatformSize(): Size

@Composable
expect fun getPlatformCachePath(): String