package com.rcudev.posts.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class SettingsViewModel(
    private val presenter: SettingsPresenter
) : ViewModel() {

    private val events = Channel<SettingsEvent>(Channel.UNLIMITED)

    val state: StateFlow<SettingsState> = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        presenter.present(events.receiveAsFlow())
    }

    fun selectPostType(postType: String) {
        events.trySend(SettingsEvent.SelectPostType(postType))
    }

    fun selectNewsSite(newsSite: String) {
        events.trySend(SettingsEvent.SelectNewsSite(newsSite))
    }

    fun toggleDarkMode(isEnabled: Boolean) {
        events.trySend(SettingsEvent.ToggleDarkMode(isEnabled))
    }

    override fun onCleared() {
        super.onCleared()
        events.close()
    }
}
