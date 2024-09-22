package com.rcudev.posts.di

import com.rcudev.posts.data.remote.InfoService
import com.rcudev.posts.data.remote.PostService
import com.rcudev.posts.ui.posts.PostViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val services = module {
    singleOf(::PostService)
    singleOf(::InfoService)
}

private val viewModels = module {
    singleOf(::PostViewModel)
}

val diModules = listOf(
    services,
    viewModels
)
