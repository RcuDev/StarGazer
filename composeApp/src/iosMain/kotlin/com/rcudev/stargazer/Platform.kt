package com.rcudev.stargazer

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun getPlatformSize() = with(LocalDensity.current) {
    Size(
        LocalWindowInfo.current.containerSize.width.toDp().value,
        LocalWindowInfo.current.containerSize.height.toDp().value
    )
}