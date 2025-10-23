package com.rcudev.posts.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.rcudev.posts.data.remote.InfoServiceImpl
import com.rcudev.posts.data.remote.PostServiceImpl
import com.rcudev.posts.domain.InfoService
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.ui.posts.PostPresenter
import com.rcudev.storage.dataStoragePreferences
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@Composable
private fun scope(): Module {
    val scope = rememberCoroutineScope()

    return module {
        single { scope }
    }
}

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

private val presenters = module {
    singleOf(::PostPresenter)
}

@Composable
fun getDiModules() = listOf(
    scope(),
    preferences(),
    services,
    presenters
)
