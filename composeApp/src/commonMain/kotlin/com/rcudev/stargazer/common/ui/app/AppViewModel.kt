package com.rcudev.stargazer.common.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcudev.stargazer.common.data.remote.InfoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class AppViewModel(
    private val infoService: InfoService
) : ViewModel() {

    val newsSites: StateFlow<List<String>>
        field = MutableStateFlow(listOf())

    init {
        loadInfo()
    }

    private fun loadInfo() = viewModelScope.launch {
        infoService.getInfo().fold(onSuccess = { data ->
            newsSites.value = data.newsSites
        }, onFailure = {
            // No-op
        })
    }
}