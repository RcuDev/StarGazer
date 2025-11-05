package com.rcudev.stargazer.di

import com.rcudev.stargazer.ui.components.topbar.TopBarPresenter
import com.rcudev.stargazer.ui.components.topbar.TopBarViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val presenters = module {
    singleOf(::TopBarPresenter)
}

private val viewModels = module {
    singleOf(::TopBarViewModel)
}

fun getDiModules() = listOf(
    presenters,
    viewModels
)