package com.yotfr.sunmoon.presentation.task.scheduled_task_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.task.*
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.event.ScheduledTaskListEvent
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.event.ScheduledTaskListUiEvent
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.mapper.ScheduledTaskListMapper
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledCompletedHeaderStateModel
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledFooterModel
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskDeleteOption
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListUiStateModel
import com.yotfr.sunmoon.presentation.utils.Quadruple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScheduledTaskListViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val scheduledTaskListMapper = ScheduledTaskListMapper()

    // selected date in horizontal calendar
    private val _selectedCalendarDate = MutableStateFlow(getCurrentDay())
    val selectedCalendarDate = _selectedCalendarDate.asStateFlow()

    // timeFormat from dataStore
    private val _timeFormat = MutableStateFlow(0)
    val timeFormat = _timeFormat.asStateFlow()

    // state for expand/collapse header in list
    private val completedTasksHeaderState = MutableStateFlow(
        ScheduledCompletedHeaderStateModel()
    )

    // state for hide/show footer in list
    private val scheduledFooterState = MutableStateFlow(
        ScheduledFooterModel()
    )

    // state for search view
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // state for taskList
    private val _uiState = MutableStateFlow<ScheduledTaskListUiStateModel?>(null)
    val uiState = _uiState.asStateFlow()

    // uiEvents channel
    private val _uiEvent = Channel<ScheduledTaskListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        // collect time format from dataStore
        viewModelScope.launch {
            dataStoreRepository.getTimeFormat().collect {
                _timeFormat.value = it ?: 2
            }
        }
        // get data for taskList state
        viewModelScope.launch {
            combine(
                taskUseCase.getScheduledTaskList(
                    selectedDate = _selectedCalendarDate,
                    searchQuery = _searchQuery,
                    currentDate = getCurrentDay()
                ),
                completedTasksHeaderState,
                dataStoreRepository.getTimePattern()
            ) { tasks, headerState, timeFormat ->
                Quadruple(tasks.first, tasks.second, headerState, timeFormat)
            }.collect { state ->
                Log.d("TEST", "collected $state")
                val sdfTest = SimpleDateFormat("yyyy dd MM", Locale.getDefault())
                Log.d("TEST", "curDate ${selectedCalendarDate.value}")
                changeHeaderVisibility(state.second.isNotEmpty())
                _uiState.value = ScheduledTaskListUiStateModel(
                    uncompletedTasks = scheduledTaskListMapper.fromDomainList(
                        state.first,
                        state.fourth
                    ),
                    completedTasks = if (completedTasksHeaderState.value.isExpanded) {
                        scheduledTaskListMapper.fromDomainList(state.second, state.fourth)
                    } else emptyList(),
                    headerState = state.third,
                    footerState = scheduledFooterState.value.copy(
                        isVisible = (state.first.isEmpty() && state.second.isEmpty())
                    )
                )
            }
        }
    }

    // method for fragment to communicate with viewModel
    fun onEvent(event: ScheduledTaskListEvent) {
        when (event) {
            is ScheduledTaskListEvent.ChangeTaskCompletionStatus -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskCompletionState(
                        task = scheduledTaskListMapper.toDomain(
                            event.task
                        )
                    )
                }
            }
            is ScheduledTaskListEvent.ChangeSelectedDate -> {
                viewModelScope.launch {
                    _selectedCalendarDate.value = event.newSelectedDate
                }
            }
            is ScheduledTaskListEvent.ChangeTaskScheduledTime -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskScheduledTime(
                        task = scheduledTaskListMapper.toDomain(
                            event.task
                        ),
                        newTime = event.newTime
                    )
                }
            }
            is ScheduledTaskListEvent.UpdateSearchQuery -> {
                viewModelScope.launch {
                    _searchQuery.value = event.searchQuery
                }
            }
            is ScheduledTaskListEvent.TrashScheduledTask -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        scheduledTaskListMapper.toDomain(event.task)
                    )
                }
                sendToUi(
                    ScheduledTaskListUiEvent.UndoTrashScheduledTask(
                        task = event.task.copy(
                            isTrashed = true
                        )
                    )
                )
            }
            is ScheduledTaskListEvent.UndoTrashTask -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        scheduledTaskListMapper.toDomain(event.task)
                    )
                }
            }
            is ScheduledTaskListEvent.ChangeCompletedTasksVisibility -> {
                completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
                    isExpanded = !completedTasksHeaderState.value.isExpanded
                )
            }
            is ScheduledTaskListEvent.DeleteTasks -> {
                viewModelScope.launch {
                    when (event.deleteOption) {
                        ScheduledTaskDeleteOption.ALL_SCHEDULED -> {
                            taskUseCase.deleteScheduledTasks()
                        }
                        ScheduledTaskDeleteOption.ALL_SCHEDULED_FOR_SELECTED_DAY -> {
                            taskUseCase.deleteScheduledTasksForSelectedDate(
                                selectedDate = getCurrentDay()
                            )
                        }
                        ScheduledTaskDeleteOption.ALL_COMPLETED_SCHEDULED -> {
                            taskUseCase.deleteAllScheduledCompletedTasks()
                        }
                        ScheduledTaskDeleteOption.ALL_SCHEDULED_COMPLETED_FOR_SELECTED_DAY -> {
                            taskUseCase.deleteScheduledCompletedTasksForSelectedDateUseCase(
                                selectedDate = getCurrentDay()
                            )
                        }
                    }
                }
            }
            is ScheduledTaskListEvent.ChangeScheduledTaskImportance -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskImportanceState(
                        task = scheduledTaskListMapper.toDomain(
                            event.task
                        )
                    )
                }
            }
            is ScheduledTaskListEvent.SelectCalendarDate -> {
                sendToUi(
                    ScheduledTaskListUiEvent.SelectCalendarDate(
                        selectedDate = event.selectedDate
                    )
                )
            }
        }
    }

    // send uiEvents to uiEvent channel
    private fun sendToUi(event: ScheduledTaskListUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    // collapse/expand header
    private fun changeHeaderVisibility(isVisible: Boolean) {
        completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
            isVisible = isVisible
        )
    }

    // get beginning of current date
    private fun getCurrentDay(): Long {
        val currentDayCalendar = Calendar.getInstance(Locale.getDefault())
        currentDayCalendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return currentDayCalendar.timeInMillis
    }
}
