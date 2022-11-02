package com.yotfr.sunmoon.presentation.task.add_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.task.TaskUseCase
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.task.add_task.event.BottomSheetAddTaskEvent
import com.yotfr.sunmoon.presentation.task.add_task.event.BottomSheetAddTaskUiEvent
import com.yotfr.sunmoon.presentation.task.add_task.mapper.AddTaskMapper
import com.yotfr.sunmoon.presentation.task.add_task.model.AddTaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetAddTaskViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase,
    private val dataStoreRepository: DataStoreRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val addTaskMapper = AddTaskMapper()

    private val selectedDate = state.get<Long>("selectedDate")

    private val _timePattern = MutableStateFlow("HH:mm")
    val timePattern = _timePattern.asStateFlow()

    private val _timeFormat = MutableStateFlow(2)
    val timeFormat = _timeFormat.asStateFlow()

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = Channel<BottomSheetAddTaskUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            dataStoreRepository.getTimePattern().collect {
                _timePattern.value = it
            }
        }
        viewModelScope.launch {
            dataStoreRepository.getTimeFormat().collect {
                _timeFormat.value = it ?: 2
            }
        }
        _uiState.value = AddTaskUiState(
            selectedDate = initState(selectedDate)
        )
    }


    fun onEvent(event: BottomSheetAddTaskEvent) {
        when (event) {
            is BottomSheetAddTaskEvent.AddTask -> {
                viewModelScope.launch {
                    taskUseCase.addTask(
                        addTaskMapper.toDomain(
                            _uiState.value.copy(
                                taskDescription = event.taskDescription
                            )
                        )
                    )
                }
                sendToUi(BottomSheetAddTaskUiEvent.PopBackStack)
            }
            is BottomSheetAddTaskEvent.DateTimeChanged -> {
                viewModelScope.launch {
                    event.newDate?.let { newDate ->
                        _uiState.value = _uiState.value.copy(selectedDate = newDate)
                    }
                    event.newTime?.let { newTime ->
                        _uiState.value = _uiState.value.copy(selectedTime = newTime)
                    }
                }
            }

            is BottomSheetAddTaskEvent.NavigateToDateSelector -> {
                viewModelScope.launch {
                    _uiEvents.send(
                        BottomSheetAddTaskUiEvent.NavigateToDateSelector(
                            date = _uiState.value.selectedDate,
                            time = _uiState.value.selectedTime
                        )
                    )
                }
            }

            is BottomSheetAddTaskEvent.ChangeTime -> {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(
                        selectedTime = event.newTime
                    )
                }
            }

            is BottomSheetAddTaskEvent.ClearDateTime -> {
                _uiState.value = _uiState.value.copy(
                    selectedDate = null,
                    selectedTime = null
                )
            }

        }
    }




    private fun sendToUi(event: BottomSheetAddTaskUiEvent) {
        viewModelScope.launch {
            _uiEvents.send(event)
        }
    }

    private fun initState(selectedDate: Long?): Long? {
        return if (selectedDate == BottomSheetAddTaskFragment.WITHOUT_SELECTED_DATE) null
        else selectedDate
    }
}