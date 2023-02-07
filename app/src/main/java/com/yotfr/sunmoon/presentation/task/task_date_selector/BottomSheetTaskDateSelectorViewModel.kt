package com.yotfr.sunmoon.presentation.task.task_date_selector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.repository.datastore.DataStoreRepository
import com.yotfr.sunmoon.presentation.task.add_task.BottomSheetAddTaskFragment
import com.yotfr.sunmoon.presentation.task.task_date_selector.event.BottomSheetDateSelectorUiEvent
import com.yotfr.sunmoon.presentation.task.task_date_selector.event.BottomSheetTaskDateSelectorEvent
import com.yotfr.sunmoon.presentation.task.task_date_selector.model.DateSelectorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetTaskDateSelectorViewModel @Inject constructor(
    state: SavedStateHandle,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val selectedDate = state.get<Long>("selectedDate")
    private val selectedTime = state.get<Long>("selectedTime")

    private val _timePattern = MutableStateFlow("HH:mm")
    val timePattern = _timePattern.asStateFlow()

    private val _timeFormat = MutableStateFlow(2)
    val timeFormat = _timeFormat.asStateFlow()

    private val isClearNeeded = MutableStateFlow(true)

    private val _uiState = MutableStateFlow(DateSelectorUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<BottomSheetDateSelectorUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            dataStoreRepository.getTimePattern().collect {
                _timePattern.value = it
                initState()
            }
        }
        viewModelScope.launch {
            dataStoreRepository.getTimeFormat().collect {
                _timeFormat.value = it ?: 2
                initState()
            }
        }
    }

    fun onEvent(event: BottomSheetTaskDateSelectorEvent) {
        when (event) {
            is BottomSheetTaskDateSelectorEvent.DateTimeChanged -> {
                _uiState.value
                event.date?.let {
                    _uiState.update {
                        it.copy(
                            selectedDate = event.date
                        )
                    }
                }
                event.time?.let {
                    _uiState.update {
                        it.copy(
                            selectedTime = event.time
                        )
                    }
                }
            }
            is BottomSheetTaskDateSelectorEvent.SaveDateTimePressed -> {
                sendToUi(
                    BottomSheetDateSelectorUiEvent.SaveDateTime(
                        date = _uiState.value.selectedDate ?: BottomSheetAddTaskFragment.WITHOUT_SELECTED_DATE,
                        time = _uiState.value.selectedTime ?: BottomSheetAddTaskFragment.WITHOUT_SELECTED_TIME
                    )
                )
            }
            is BottomSheetTaskDateSelectorEvent.ClearDateTime -> {
                if (isClearNeeded.value) {
                    _uiState.value = DateSelectorUiState(
                        selectedDate = null,
                        selectedTime = null
                    )
                }
                isClearNeeded.value = true
            }
            is BottomSheetTaskDateSelectorEvent.ChangeClearNeeded -> {
                isClearNeeded.value = event.isNeeded
            }
        }
    }

    private fun sendToUi(uiEvent: BottomSheetDateSelectorUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(uiEvent)
        }
    }

    private fun initState() {
        val date = if (selectedDate == BottomSheetTaskDateSelectorFragment.WITHOUT_DATE) null
        else selectedDate
        val time = if (selectedTime == BottomSheetTaskDateSelectorFragment.WITHOUT_TIME) null
        else selectedTime
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedTime = time
        )
    }
}
