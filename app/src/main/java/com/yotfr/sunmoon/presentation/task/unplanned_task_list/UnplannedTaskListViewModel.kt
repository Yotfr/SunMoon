package com.yotfr.sunmoon.presentation.task.unplanned_task_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.task.TaskUseCase
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.event.UnplannedTaskListEvent
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.event.UnplannedTaskListUiEvent
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.mapper.UnplannedTaskListMapper
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedCompletedHeaderStateModel
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedDeleteOption
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedFooterModel
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListUiStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnplannedTaskListViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase
) : ViewModel() {

    private val unplannedTaskListMapper = UnplannedTaskListMapper()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val completedTasksHeaderState = MutableStateFlow(
        UnplannedCompletedHeaderStateModel()
    )

    private val unplannedFooterState = MutableStateFlow(
        UnplannedFooterModel()
    )

    private val _uiState = MutableStateFlow<UnplannedTaskListUiStateModel?>(null)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UnplannedTaskListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                taskUseCase.getUnplannedTaskList(
                    searchQuery = _searchQuery,
                ),
                completedTasksHeaderState
            ) { tasks, headerState ->
                Triple(tasks.first, tasks.second, headerState)
            }.collect { state ->
                changeHeaderVisibility(state.second.isNotEmpty())
                _uiState.value = UnplannedTaskListUiStateModel(
                    uncompletedTasks = unplannedTaskListMapper.fromDomainList(state.first),
                    completedTasks = if (completedTasksHeaderState.value.isExpanded) {
                        unplannedTaskListMapper.fromDomainList(state.second)
                    } else emptyList(),
                    headerState = state.third,
                    footerState = unplannedFooterState.value.copy(
                        isVisible = (state.first.isEmpty() && state.second.isEmpty())
                    )
                )
            }
        }
    }


    fun onEvent(event: UnplannedTaskListEvent) {
        when (event) {
            is UnplannedTaskListEvent.ChangeUnplannedTaskCompletionStatus -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskCompletionState(
                        task = unplannedTaskListMapper.toDomain(
                            event.task
                        )
                    )
                }
            }
            is UnplannedTaskListEvent.UpdateSearchQuery -> {
                _searchQuery.value = event.searchQuery
            }
            is UnplannedTaskListEvent.ScheduleTask -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskScheduledDateTime(
                        task = unplannedTaskListMapper.toDomain(
                            event.task
                        ),
                        newDate = event.selectedDate,
                        newTime = event.selectedTime
                    )
                }
            }
            is UnplannedTaskListEvent.TrashUnplannedTask -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        task = unplannedTaskListMapper.toDomain(event.task)
                    )
                }
                sendToUi(
                    UnplannedTaskListUiEvent.UndoDeleteUnplannedTask(
                        task = event.task.copy(
                            isTrashed = true
                        )
                    )
                )
            }
            is UnplannedTaskListEvent.UndoTrashItem -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        task = unplannedTaskListMapper.toDomain(event.task)
                    )
                }
            }
            is UnplannedTaskListEvent.ChangeCompletedTasksVisibility -> {
                completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
                    isExpanded = !completedTasksHeaderState.value.isExpanded
                )
            }

            is UnplannedTaskListEvent.DeleteTasks -> {
                viewModelScope.launch {
                    when (event.deleteOption) {
                        UnplannedDeleteOption.ALL_UNPLANNED -> {
                            taskUseCase.deleteUnplanned
                        }
                        UnplannedDeleteOption.ALL_UNPLANNED_COMPLETED -> {
                            taskUseCase.deleteAllUnplannedCompletedTasks
                        }
                    }
                }
            }
            is UnplannedTaskListEvent.ChangeUnplannedTaskImportance -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskImportanceState(
                        task = unplannedTaskListMapper.toDomain(
                            event.task
                        )
                    )
                }
            }
        }
    }

    private fun changeHeaderVisibility(isVisible: Boolean) {
        completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
            isVisible = isVisible
        )
    }

    private fun sendToUi(event: UnplannedTaskListUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}