package com.rcudev.stargazer.ui.components.topbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

class TopBarViewModel(
    private val presenter: TopBarPresenter
) : ViewModel() {

    private val events = MutableSharedFlow<TopBarEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val state: StateFlow<TopBarState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        presenter.present(events)
    }

    fun selectPostType(postType: String) {
        events.tryEmit(TopBarEvent.SelectPostType(postType))
    }
}
