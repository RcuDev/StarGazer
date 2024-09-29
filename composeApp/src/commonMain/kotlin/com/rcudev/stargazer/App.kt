package com.rcudev.stargazer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rcudev.ds.theme.DarkColorScheme
import com.rcudev.ds.theme.LightColorScheme
import com.rcudev.ds.theme.UiTheme
import com.rcudev.posts.di.getDiModules
import com.rcudev.stargazer.ui.AppContent
import com.rcudev.storage.DARK_MODE
import com.rcudev.utils.GetImageLoader
import com.rcudev.utils.LocalImageLoader
import com.rcudev.utils.LocalScreenSize
import com.rcudev.utils.getPlatformSize
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
@Preview
internal fun App() {

    val diModules = getDiModules()

    KoinApplication(application = {
        modules(diModules)
    }) {
        val preferences: DataStore<Preferences> = koinInject()
        val colorScheme = preferences.data.map { prefs ->
            if (prefs[DARK_MODE] == true) DarkColorScheme else LightColorScheme
        }.collectAsState(DarkColorScheme)

        UiTheme(
            colorScheme = colorScheme.value
        ) {
            CompositionLocalProvider(LocalScreenSize provides getPlatformSize()) {
                CompositionLocalProvider(LocalImageLoader provides GetImageLoader()) {
                    AppContent()
                }
            }
        }
    }
}