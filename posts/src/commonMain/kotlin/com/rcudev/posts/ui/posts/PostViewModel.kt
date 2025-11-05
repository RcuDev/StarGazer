package com.rcudev.posts.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class PostViewModel(
    private val presenter: PostPresenter
) : ViewModel() {

    private val events = Channel<PostEvent>(Channel.UNLIMITED)

    val state: StateFlow<PostState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        presenter.present(events.receiveAsFlow())
    }

    fun loadNextPage() {
        events.trySend(PostEvent.LoadNextPage)
    }

    fun retry() {
        events.trySend(PostEvent.Retry)
    }

    fun dismissLoadPageError() {
        events.trySend(PostEvent.DismissLoadPageError)
    }

    override fun onCleared() {
        super.onCleared()
        events.close()
    }

}