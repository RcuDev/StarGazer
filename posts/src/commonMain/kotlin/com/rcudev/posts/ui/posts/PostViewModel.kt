package com.rcudev.posts.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModel(
    private val presenter: PostPresenter
) : ViewModel() {

    private val events = MutableSharedFlow<PostEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    private val presentationResult = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        presenter.present(events)  // ✅ Solo se llama UNA vez
    }

    val state: StateFlow<PostState> = presentationResult
        .map { it.first }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostState.Loading
        )

    val effects: Flow<PostEffect> = presentationResult
        .flatMapLatest { it.second }

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