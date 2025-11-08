package com.rcudev.posts.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val presenter: SettingsPresenter
) : ViewModel() {

    private val events = MutableSharedFlow<SettingsEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    private val presentationResult = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        presenter.present(events)
    }

    val state: StateFlow<SettingsState> = presentationResult
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsState.Loading
        )

    fun selectNewsSite(newsSite: String) {
        events.tryEmit(SettingsEvent.SelectNewsSite(newsSite))
    }

    fun toggleDarkMode(isEnabled: Boolean) {
        events.tryEmit(SettingsEvent.ToggleDarkMode(isEnabled))
    }
}
