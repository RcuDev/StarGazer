package com.rcudev.stargazer.ui.components.topbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.rcudev.posts.ui.settings.SettingsState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TopBarViewModel(
    private val presenter: TopBarPresenter
) : ViewModel() {

    private val events = MutableSharedFlow<TopBarEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    private val presentationResult = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        presenter.present(events)
    }

    val state: StateFlow<TopBarState> = presentationResult
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TopBarState.Idle
        )

    fun selectPostType(postType: String) {
        events.tryEmit(TopBarEvent.SelectPostType(postType))
    }
}
