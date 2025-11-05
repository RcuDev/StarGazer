package com.rcudev.posts.ui.posts

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

class PostViewModel(
    private val presenter: PostPresenter
) : ViewModel() {

    private val events = MutableSharedFlow<PostEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val _effects = MutableSharedFlow<PostEffect>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects: Flow<PostEffect> = _effects


    val state: StateFlow<PostState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        val (state, effects) = presenter.present(events)

        LaunchedEffect(Unit) {
            effects.collect { effect ->
                _effects.emit(effect)
            }
        }

        state

    }

    fun loadNextPage() {
        events.tryEmit(PostEvent.LoadNextPage)
    }

    fun retry() {
        events.tryEmit(PostEvent.Retry)
    }

    fun dismissLoadPageError() {
        events.tryEmit(PostEvent.DismissLoadPageError)
    }

    fun onPostClick(url: String) {
        events.tryEmit(PostEvent.OnPostClick(url))
    }
}