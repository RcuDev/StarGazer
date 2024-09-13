package com.rcudev.stargazer.common.di

import com.rcudev.stargazer.articles.ui.ArticlesViewModel
import com.rcudev.stargazer.blogs.ui.BlogsViewModel
import com.rcudev.stargazer.common.data.remote.InfoService
import com.rcudev.stargazer.common.data.remote.PostService
import com.rcudev.stargazer.common.ui.app.AppViewModel
import com.rcudev.stargazer.reports.ui.ReportsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val services = module {
    singleOf(::PostService)
    singleOf(::InfoService)
}

private val viewModels = module {
    singleOf(::AppViewModel)
    singleOf(::ArticlesViewModel)
    singleOf(::BlogsViewModel)
    singleOf(::ReportsViewModel)
}

val diModules = listOf(
    services,
    viewModels
)
