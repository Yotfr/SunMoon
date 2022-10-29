package com.yotfr.sunmoon.presentation.settings.settings_root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.repository.sharedpreference.PreferencesHelper
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsEvent
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesHelper: PreferencesHelper
):ViewModel() {

    private val _uiEvent = Channel<SettingsUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.ChangeDateFormat -> {
                preferencesHelper.updateDateFormat(
                    dateFormat = event.dateFormat
                )
                sendToUi(SettingsUiEvent.ShowChangeRestartSnackbar)
            }
            is SettingsEvent.ChangeTimeFormat -> {
                preferencesHelper.updateTimeFormat(
                    timeFormat = event.timeFormat
                )
                sendToUi(SettingsUiEvent.ShowChangeRestartSnackbar)
            }
        }
    }

    private fun sendToUi(uiEvent: SettingsUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(uiEvent)
        }
    }


}
