package com.rcudev.stargazer

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration

@Composable
internal actual fun getPlatformSize() =
    Size(
        LocalConfiguration.current.screenWidthDp.toFloat(),
        LocalConfiguration.current.screenHeightDp.toFloat()
    )