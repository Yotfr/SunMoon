package com.yotfr.sunmoon.presentation.trash.trash_task_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.task.*
import com.yotfr.sunmoon.domain.repository.sharedpreference.PreferencesHelper
import com.yotfr.sunmoon.presentation.trash.trash_task_list.event.TrashTaskEvent
import com.yotfr.sunmoon.presentation.trash.trash_task_list.event.TrashTaskUiEvent
import com.yotfr.sunmoon.presentation.trash.trash_task_list.mapper.TrashedTaskListMapper
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashTaskFooterModel
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedCompletedHeaderStateModel
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedTaskUiStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashTaskViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase,
    preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val trashedTaskListMapper = TrashedTaskListMapper()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val completedTasksHeaderState = MutableStateFlow(
        TrashedCompletedHeaderStateModel()
    )

    //TODO:Change shared prefs to dataStore
    private val _sdfPattern = MutableStateFlow(preferencesHelper.getTimeFormat())
    val sdfPattern = _sdfPattern.asStateFlow()

    private val _uiState = MutableStateFlow<TrashedTaskUiStateModel?>(null)
    val uiState = _uiState.asSharedFlow()

    private val _uiEvent = Channel<TrashTaskUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                taskUseCase.getTrashedTaskListUseCase(
                    searchQuery = _searchQuery
                ),
                completedTasksHeaderState
            ) { tasks, headerState ->
                Triple(tasks.first, tasks.second, headerState)
            }.collect { state ->
                changeHeaderVisibility(state.second.isNotEmpty())
                _uiState.value = TrashedTaskUiStateModel(
                    uncompletedTasks = trashedTaskListMapper.fromDomainList(
                        state.first,
                        sdfPattern = _sdfPattern.value
                    ),
                    completedTasks = if (completedTasksHeaderState.value.isExpanded) {
                        trashedTaskListMapper.fromDomainList(
                            state.second,
                            sdfPattern = _sdfPattern.value
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
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        task = trashedTaskListMapper.toDomain(
                            event.task
                        )
                    )
                }
                sendToUi(TrashTaskUiEvent.ShowRestoreSnackbar)
            }
            is TrashTaskEvent.UndoDeleteTrashedTask -> {
                Log.d("UNDO", "${event.task}")
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
        }
    }

    private fun sendToUi(event: TrashTaskUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun changeHeaderVisibility(isVisible: Boolean) {
        completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
            isVisible = isVisible
        )
    }

}