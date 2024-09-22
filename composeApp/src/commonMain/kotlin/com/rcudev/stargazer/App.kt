package com.rcudev.stargazer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.rcudev.ds.theme.UiTheme
import com.rcudev.posts.di.diModules
import com.rcudev.stargazer.ui.AppContent
import com.rcudev.utils.GetImageLoader
import com.rcudev.utils.LocalImageLoader
import com.rcudev.utils.LocalScreenSize
import com.rcudev.utils.getPlatformSize
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
internal fun App() {
    KoinApplication(application = {
        modules(diModules)
    }) {
        UiTheme {
            CompositionLocalProvider(LocalScreenSize provides getPlatformSize()) {
                CompositionLocalProvider(LocalImageLoader provides GetImageLoader()) {
                    AppContent()
                }
            }
        }
    }
}