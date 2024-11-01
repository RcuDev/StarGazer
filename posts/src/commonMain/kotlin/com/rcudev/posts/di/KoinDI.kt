package com.rcudev.posts.di

import androidx.compose.runtime.Composable
import com.rcudev.posts.data.remote.InfoServiceImpl
import com.rcudev.posts.data.remote.PostServiceImpl
import com.rcudev.posts.domain.InfoService
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.ui.posts.PostViewModel
import com.rcudev.storage.dataStoragePreferences
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@Composable
private fun preferences(): Module {
    val dataStorage = dataStoragePreferences()

    return module {
        single { dataStorage }
    }
}

private val services = module {
    singleOf<PostService>(::PostServiceImpl)
    singleOf<InfoService>(::InfoServiceImpl)
}

private val viewModels = module {
    singleOf(::PostViewModel)
}

@Composable
fun getDiModules() = listOf(
    preferences(),
    services,
    viewModels
)
