package com.rcudev.stargazer

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(
    finishSplash: () -> Unit
) = ComposeUIViewController { App {
    finishSplash()
} }
