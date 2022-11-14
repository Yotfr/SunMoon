package com.yotfr.sunmoon.presentation.trash.trash_task_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.task.*
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.trash.trash_task_list.event.TrashTaskEvent
import com.yotfr.sunmoon.presentation.trash.trash_task_list.event.TrashTaskUiEvent
import com.yotfr.sunmoon.presentation.trash.trash_task_list.mapper.TrashedTaskListMapper
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashTaskFooterModel
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedCompletedHeaderStateModel
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedTaskUiStateModel
import com.yotfr.sunmoon.presentation.utils.Quadruple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TrashTaskViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val trashedTaskListMapper = TrashedTaskListMapper()

    //state for search view
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    //state for timeFormat from dataStore
    private val _timeFormat = MutableStateFlow(0)
    val timeFormat = _timeFormat.asStateFlow()

    //state for header
    private val completedTasksHeaderState = MutableStateFlow(
        TrashedCompletedHeaderStateModel()
    )

    private val _uiState = MutableStateFlow<TrashedTaskUiStateModel?>(null)
    val uiState = _uiState.asSharedFlow()

    //channel for uiEvents
    private val _uiEvent = Channel<TrashTaskUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        //get timeFormat from data store
        viewModelScope.launch {
            dataStoreRepository.getTimeFormat().collect{
                _timeFormat.value = it ?: 2
            }
        }
        //get trashed task
        viewModelScope.launch {
            combine(
                taskUseCase.getTrashedTaskListUseCase(
                    searchQuery = _searchQuery
                ),
                completedTasksHeaderState,
                dataStoreRepository.getTimePattern()
            ) { tasks, headerState, timePattern ->
                Quadruple(tasks.first, tasks.second, headerState, timePattern)
            }.collect { state ->
                changeHeaderVisibility(state.second.isNotEmpty())
                _uiState.value = TrashedTaskUiStateModel(
                    uncompletedTasks = trashedTaskListMapper.fromDomainList(
                        state.first,
                        sdfPattern = state.fourth
                    ),
                    completedTasks = if (completedTasksHeaderState.value.isExpanded) {
                        trashedTaskListMapper.fromDomainList(
                            state.second,
                            sdfPattern = state.fourth
                        )
                    } else emptyList(),
                    headerState = state.third,
                    footerState = TrashTaskFooterModel(
                        isVisible = (state.first.isEmpty() && state.second.isEmpty())
                    )
                )
            }
        }
    }

    //method for fragment to communicate with viewModel
    fun onEvent(event: TrashTaskEvent) {
        when (event) {
            is TrashTaskEvent.ChangeCompletedTasksVisibility -> {
                completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
                    isExpanded = !completedTasksHeaderState.value.isExpanded
                )
            }
            is TrashTaskEvent.UpdateSearchQuery -> {
                _searchQuery.value = event.searchQuery
            }
            is TrashTaskEvent.DeleteTrashedTask -> {
                viewModelScope.launch {
                    taskUseCase.deleteTaskUseCase(
                        task = trashedTaskListMapper.toDomain(
                            event.task
                        )
                    )
                }

                sendToUi(
                    TrashTaskUiEvent.ShowUndoDeleteSnackbar(
                        task = event.task
                    )
                )

            }
            is TrashTaskEvent.RestoreTrashedTask -> {
                if (
                    event.task.scheduledDate != null && event.task.scheduledDate < getCurrentDay()
                ) {
                    sendToUi(TrashTaskUiEvent.ShowDateTimeChangeDialog(
                        task = event.task
                    ))
                }else {
                    viewModelScope.launch {
                        taskUseCase.trashUntrashTask(
                            task = trashedTaskListMapper.toDomain(
                                event.task
                            )
                        )
                    }
                    sendToUi(TrashTaskUiEvent.ShowRestoreSnackbar(
                        task = event.task
                    ))
                }
            }
            is TrashTaskEvent.UndoDeleteTrashedTask -> {
                viewModelScope.launch {
                    taskUseCase.addTask(
                        task = trashedTaskListMapper.toDomain(
                            event.task
                        )
                    )
                }
            }
            TrashTaskEvent.DeleteAllTrashedCompletedTask -> {
                viewModelScope.launch {
                    taskUseCase.deleteAllCompletedTrashedTasksUseCase()
                }
            }
            TrashTaskEvent.DeleteAllTrashedTask -> {
                viewModelScope.launch {
                    taskUseCase.deleteAllTrashedTasksUseCase()
                }
            }
            is TrashTaskEvent.RestoreTrashedTaskWithDateTimeChanged -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        task = trashedTaskListMapper.toDomain(
                            event.task.copy(
                                scheduledDate = event.date,
                                scheduledTime = event.time ?: event.task.scheduledTime
                            )
                        )
                    )
                }
            }
            is TrashTaskEvent.DeleteRelatedTasks -> {
                viewModelScope.launch {
                    taskUseCase.deleteAllRelatedSubTasks(
                        taskId = event.task.taskId
                    )
                }
            }
        }
    }

    //send uiEvents to uiEvent channel
    private fun sendToUi(event: TrashTaskUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    //expand or collapse header
    private fun changeHeaderVisibility(isVisible: Boolean) {
        completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
            isVisible = isVisible
        )
    }

    //get beginning of current day
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