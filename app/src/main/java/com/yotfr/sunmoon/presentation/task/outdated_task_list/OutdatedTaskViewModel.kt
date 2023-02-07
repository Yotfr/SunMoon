package com.yotfr.sunmoon.presentation.task.outdated_task_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.usecase.task.TaskUseCase
import com.yotfr.sunmoon.domain.repository.datastore.DataStoreRepository
import com.yotfr.sunmoon.presentation.task.outdated_task_list.event.OutdatedTaskEvent
import com.yotfr.sunmoon.presentation.task.outdated_task_list.event.OutdatedTaskUiEvent
import com.yotfr.sunmoon.presentation.task.outdated_task_list.mapper.OutdatedTaskListMapper
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedCompletedHeaderStateModel
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedDeleteOption
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedFooterModel
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListUiStateModel
import com.yotfr.sunmoon.presentation.utils.Quadruple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OutdatedTaskViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val outdatedTaskListMapper = OutdatedTaskListMapper()

    // state for search view
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // timeFormat from dataStore
    private val _timeFormat = MutableStateFlow(0)
    val timeFormat = _timeFormat.asStateFlow()

    // state for expand/collapse header in list
    private val completedTasksHeaderState = MutableStateFlow(
        OutdatedCompletedHeaderStateModel()
    )

    // state for hide/show footer in list
    private val outdatedFooterState = MutableStateFlow(
        OutdatedFooterModel()
    )

    // beginning of the current day
    private val currentDate = getCurrentDate()

    // state for taskList
    private val _uiState = MutableStateFlow<OutdatedTaskListUiStateModel?>(null)
    val uiState = _uiState.asStateFlow()

    // uiEvents channel
    private val _uiEvent = Channel<OutdatedTaskUiEvent>()
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
                taskUseCase.getOutdatedTaskList(
                    searchQuery = _searchQuery,
                    currentDate = getCurrentDate()
                ),
                completedTasksHeaderState,
                dataStoreRepository.getTimePattern()
            ) { tasks, headerState, timePattern ->
                Quadruple(tasks.first, tasks.second, headerState, timePattern)
            }.collect { state ->
                changeHeaderVisibility(state.second.isNotEmpty())
                _uiState.value = OutdatedTaskListUiStateModel(
                    uncompletedTasks = outdatedTaskListMapper.fromDomainList(
                        state.first,
                        state.fourth,
                        currentDate
                    ),
                    completedTasks = if (completedTasksHeaderState.value.isExpanded) {
                        outdatedTaskListMapper.fromDomainList(
                            state.second,
                            state.fourth,
                            currentDate
                        )
                    } else emptyList(),
                    headerState = state.third,
                    footerState = outdatedFooterState.value.copy(
                        isVisible = (state.first.isEmpty() && state.second.isEmpty())
                    )
                )
            }
        }
    }

    // method for fragment to communicate with viewModel
    fun onEvent(event: OutdatedTaskEvent) {
        when (event) {
            is OutdatedTaskEvent.UpdateSearchQuery -> {
                _searchQuery.value = event.searchQuery
            }
            is OutdatedTaskEvent.ScheduleTask -> {
                viewModelScope.launch {
                    val task = if (event.isMakeUndoneNeeded) event.task.copy(
                        isCompleted = false
                    ) else event.task
                    taskUseCase.changeTaskScheduledDateTime(
                        task = outdatedTaskListMapper.toDomain(
                            task
                        ),
                        newDate = event.date,
                        newTime = event.time
                    )
                }
                sendToUi(
                    OutdatedTaskUiEvent.NavigateToScheduledTask(
                        taskDate = event.date
                    )
                )
            }
            is OutdatedTaskEvent.TrashOutdatedTask -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        task = outdatedTaskListMapper.toDomain(event.task)
                    )
                }
                sendToUi(
                    OutdatedTaskUiEvent.UndoDeleteOutdatedTask(
                        task = event.task.copy(
                            isTrashed = true
                        )
                    )
                )
            }
            is OutdatedTaskEvent.UndoDeleteOutdatedTask -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        task = outdatedTaskListMapper.toDomain(event.task)
                    )
                }
            }
            is OutdatedTaskEvent.ChangeCompletedTasksVisibility -> {
                completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
                    isExpanded = !completedTasksHeaderState.value.isExpanded
                )
            }
            is OutdatedTaskEvent.DeleteTasks -> {
                viewModelScope.launch {
                    when (event.deleteOption) {
                        OutdatedDeleteOption.ALL_OUTDATED -> {
                            taskUseCase.deleteOutdated(
                                expDate = currentDate
                            )
                        }
                        OutdatedDeleteOption.ALL_OUTDATED_COMPLETED -> {
                            taskUseCase.deleteAllOutdatedCompletedTasks(
                                expDate = currentDate
                            )
                        }
                    }
                }
            }
        }
    }

    // get beginning of current date
    private fun getCurrentDate(): Long {
        val currentDayCalendar = Calendar.getInstance(Locale.getDefault())
        currentDayCalendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return currentDayCalendar.timeInMillis
    }

    // collapse/expand header
    private fun changeHeaderVisibility(isVisible: Boolean) {
        completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
            isVisible = isVisible
        )
    }

    // send uiEvents to uiEvent channel
    private fun sendToUi(uiEvent: OutdatedTaskUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(uiEvent)
        }
    }
}
