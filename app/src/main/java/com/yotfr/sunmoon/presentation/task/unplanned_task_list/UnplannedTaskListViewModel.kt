package com.yotfr.sunmoon.presentation.task.unplanned_task_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.task.TaskUseCase
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.event.UnplannedTaskListEvent
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.event.UnplannedTaskListUiEvent
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.mapper.UnplannedTaskListMapper
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedCompletedHeaderStateModel
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedDeleteOption
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedFooterModel
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListUiStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnplannedTaskListViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val unplannedTaskListMapper = UnplannedTaskListMapper()

    //state for search view
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    //state for expand/collapse header in list
    private val completedTasksHeaderState = MutableStateFlow(
        UnplannedCompletedHeaderStateModel()
    )

    //timeFormat from dataStore
    private val _timeFormat = MutableStateFlow(0)
    val timeFormat = _timeFormat.asStateFlow()

    //state for hide/show footer in list
    private val unplannedFooterState = MutableStateFlow(
        UnplannedFooterModel()
    )

    //state for taskList
    private val _uiState = MutableStateFlow<UnplannedTaskListUiStateModel?>(null)
    val uiState = _uiState.asStateFlow()

    //uiEvents channel
    private val _uiEvent = Channel<UnplannedTaskListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        //collect time format from dataStore
        viewModelScope.launch {
            dataStoreRepository.getTimeFormat().collect{
                _timeFormat.value = it ?: 2
            }
        }
        //get data for taskList state
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

    //method for fragment to communicate with viewModel
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
                sendToUi(UnplannedTaskListUiEvent.NavigateToScheduledTask(
                    taskDate = event.selectedDate
                ))
            }
            is UnplannedTaskListEvent.TrashUnplannedTask -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        task = unplannedTaskListMapper.toDomain(event.task)
                    )
                }
                sendToUi(
                    UnplannedTaskListUiEvent.UndoTrashUnplannedTask(
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
                            taskUseCase.deleteUnplanned()
                        }
                        UnplannedDeleteOption.ALL_UNPLANNED_COMPLETED -> {
                            taskUseCase.deleteAllUnplannedCompletedTasks()
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

    //collapse/expand header
    private fun changeHeaderVisibility(isVisible: Boolean) {
        completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
            isVisible = isVisible
        )
    }

    //send uiEvents to uiEvent channel
    private fun sendToUi(event: UnplannedTaskListUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}