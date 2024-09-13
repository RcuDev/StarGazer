package com.rcudev.stargazer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Size

internal val LocalScreenSize = compositionLocalOf<Size> { error("No screen size provided") }

@Composable
internal expect fun getPlatformSize(): Size