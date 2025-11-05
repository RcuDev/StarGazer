package com.rcudev.posts.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

class PostViewModel(
    private val presenter: PostPresenter
) : ViewModel() {

    private val events = MutableSharedFlow<PostEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val state: StateFlow<PostState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        presenter.present(events)
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
}