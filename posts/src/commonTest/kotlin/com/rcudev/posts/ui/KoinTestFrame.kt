package com.rcudev.posts.ui

import androidx.compose.runtime.Composable
import com.rcudev.posts.di.getDiModules
import org.koin.compose.KoinApplication

@Composable
internal fun KoinTestFrame(
    content: @Composable () -> Unit
) {
    val diModules = getDiModules()

    KoinApplication(application = {
        modules(diModules)
    }) {
        content()
    }
}