package com.rcudev.stargazer.ui.components

import androidx.compose.runtime.Composable
import com.rcudev.posts.di.getPostDiModules
import com.rcudev.stargazer.di.getDiModules
import org.koin.compose.KoinApplication

@Composable
internal fun KoinTestFrame(
    content: @Composable () -> Unit
) {
    val diModules = getDiModules() + getPostDiModules()

    KoinApplication(application = {
        modules(diModules)
    }) {
        content()
    }
}