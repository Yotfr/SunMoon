package com.yotfr.sunmoon.presentation.settings.settings_root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsEvent
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsUiEvent
import com.yotfr.sunmoon.presentation.settings.settings_root.model.DatePattern
import com.yotfr.sunmoon.presentation.settings.settings_root.model.SettingsUiStateModel
import com.yotfr.sunmoon.presentation.settings.settings_root.model.TimeFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    //state for settings
    private val _settingsUiState = MutableStateFlow(SettingsUiStateModel())
    val settingsUiState = _settingsUiState.asStateFlow()

    //uiEvents channel
    private val _uiEvent = Channel<SettingsUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        //get date&time format
        viewModelScope.launch {
            combine(
                dataStoreRepository.getDateFormat(),
                dataStoreRepository.getTimeFormat()
            ) { dateFormat, timeFormat ->
                Pair(dateFormat, timeFormat)
            }.collect { prefs ->
                _settingsUiState.value = SettingsUiStateModel(
                    datePattern = DatePattern.values().find {
                        it.pattern == (prefs.first)
                    }
                        ?: DatePattern.DAY_FIRST,
                    timeFormat = TimeFormat.values().find {
                        it.format == (prefs.second ?: 2)
                    }
                        ?: TimeFormat.SYSTEM_DEFAULT
                )
            }
        }
    }

    //method for fragment to communicate with viewModel
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ChangeDateFormat -> {
                viewModelScope.launch {
                    dataStoreRepository.updateDateFormat(
                        dateFormat = event.dateFormat
                    )
                }
            }
            is SettingsEvent.ChangeTimeFormat -> {
                viewModelScope.launch {
                    dataStoreRepository.updateTimeFormat(
                        timeFormat = event.timeFormat,
                        timePattern = event.timePattern
                    )
                }
            }
        }
    }

    //send uiEvents to uiEvent channel
    //TODO:useless
    private fun sendToUi(uiEvent: SettingsUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(uiEvent)
        }
    }


}
