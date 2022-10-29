package com.yotfr.sunmoon.presentation.task.task_date_selector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    state: SavedStateHandle
) : ViewModel() {

    private val selectedDate = state.get<Long>("selectedDate")
    private val selectedTime = state.get<Long>("selectedTime")

    private val _uiState = MutableStateFlow(DateSelectorUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<BottomSheetDateSelectorUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        initState()
    }



    fun onEvent(event: BottomSheetTaskDateSelectorEvent) {
        when(event){
            is BottomSheetTaskDateSelectorEvent.DateTimeChanged -> {
                event.date?.let {
                    _uiState.value = _uiState.value.copy(
                        selectedDate = event.date
                    )
                }
                event.time?.let {
                    _uiState.value = _uiState.value.copy(
                        selectedTime = event.time
                    )
                }
            }
            is BottomSheetTaskDateSelectorEvent.SaveDateTimePressed -> {
                sendToUi(
                    BottomSheetDateSelectorUiEvent.SaveDateTime(
                    date = _uiState.value.selectedDate ?: BottomSheetAddTaskFragment.WITHOUT_SELECTED_DATE,
                    time = _uiState.value.selectedTime ?: BottomSheetAddTaskFragment.WITHOUT_SELECTED_TIME
                ))
            }
            is BottomSheetTaskDateSelectorEvent.ClearDateTime -> {
                _uiState.value = DateSelectorUiState(
                    selectedDate = null,
                    selectedTime = null
                )
            }
        }
    }

    private fun sendToUi(uiEvent: BottomSheetDateSelectorUiEvent){
        viewModelScope.launch {
            _uiEvent.send(uiEvent)
        }
    }

    private fun initState() {
        val date =  if (selectedDate == BottomSheetTaskDateSelectorFragment.WITHOUT_DATE) null
        else selectedDate
        val time =  if (selectedTime == BottomSheetTaskDateSelectorFragment.WITHOUT_TIME) null
        else selectedDate
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedTime = time
        )
    }

}