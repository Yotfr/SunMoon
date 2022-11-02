package com.yotfr.sunmoon.presentation.settings.settings_theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.repository.sharedpreference.PreferencesHelper
import com.yotfr.sunmoon.presentation.settings.settings_theme.event.SettingsThemeEvent
import com.yotfr.sunmoon.presentation.settings.settings_theme.event.SettingsThemeUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsThemeViewModel @Inject constructor(
    private val preferencesHelper: PreferencesHelper
):ViewModel() {

    private val _uiEvent = Channel<SettingsThemeUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onEvent(event:SettingsThemeEvent) {
        when (event) {
            is SettingsThemeEvent.ChangeTheme -> {
                viewModelScope.launch {
                    preferencesHelper.updateTheme(
                        theme = event.newTheme
                    )
                }
                sendToUi(SettingsThemeUiEvent.RestartActivity)
            }
        }
    }

    private fun sendToUi(uiEvent:SettingsThemeUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(uiEvent)
        }
    }
}