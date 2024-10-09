package com.rcudev.ds.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LightColorScheme = lightColorScheme(
    primary = WarmPrimary40,
    secondary = WarmSecondary40,
    background = WarmNeutral99,
    surface = WarmNeutral99,
    onPrimary = WarmPrimary100,
    onSecondary = WarmSecondary100,
    onBackground = WarmNeutral10,
    onSurface = WarmNeutral10,
    error = Error40,
    onError = Error100,
    errorContainer = Error90,
    onErrorContainer = Error10,
    surfaceVariant = WarmNeutral90,
    outline = WarmNeutral50,
    inversePrimary = WarmPrimary80,
    inverseSurface = WarmNeutral20,
    inverseOnSurface = WarmNeutral95,
    scrim = WarmNeutral0
)

val DarkColorScheme = darkColorScheme(
    primary = WarmPrimary80,
    secondary = WarmSecondary80,
    background = WarmNeutral10,
    surface = WarmNeutral10,
    onPrimary = WarmPrimary20,
    onSecondary = WarmSecondary20,
    onBackground = WarmNeutral90,
    onSurface = WarmNeutral90,
    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,
    surfaceVariant = WarmNeutral30,
    outline = WarmNeutral60,
    inversePrimary = WarmPrimary40,
    inverseSurface = WarmNeutral90,
    inverseOnSurface = WarmNeutral20,
    scrim = WarmNeutral0
)

@Composable
fun UiTheme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}