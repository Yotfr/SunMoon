package com.yotfr.sunmoon.presentation.task.scheduled_task_list

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
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScheduledTaskListViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val scheduledTaskListMapper = ScheduledTaskListMapper()

    private val _selectedCalendarDate = MutableStateFlow(getCurrentDay())
    val selectedCalendarDate = _selectedCalendarDate.asStateFlow()

    private val _timeFormat = MutableStateFlow(0)
    val timeFormat = _timeFormat.asStateFlow()

    private val completedTasksHeaderState = MutableStateFlow(
        ScheduledCompletedHeaderStateModel()
    )
    private val scheduledFooterState = MutableStateFlow(
        ScheduledFooterModel()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<ScheduledTaskListUiStateModel?>(null)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ScheduledTaskListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    init {
        viewModelScope.launch {
            dataStoreRepository.getTimeFormat().collect{
                _timeFormat.value = it ?: 2
            }
        }
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
                Quadruple(tasks.first,tasks.second,headerState,timeFormat)
            }.collect { state ->
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


    fun onEvent(event: ScheduledTaskListEvent) {
        when (event) {
            is ScheduledTaskListEvent.ChangeTaskCompletionStatus -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskCompletionState(
                        task = scheduledTaskListMapper.toDomain(
                            event.task,
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
                    ScheduledTaskListUiEvent.UndoDeleteScheduledTask(
                        task = event.task.copy(
                            isTrashed = true
                        )
                    )
                )
            }

            is ScheduledTaskListEvent.UndoTrashItem -> {
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
                            taskUseCase.deleteScheduledTasks
                        }
                        ScheduledTaskDeleteOption.ALL_SCHEDULED_FOR_SELECTED_DAY -> {
                            taskUseCase.deleteScheduledTasksForSelectedDate
                        }
                        ScheduledTaskDeleteOption.ALL_COMPLETED_SCHEDULED -> {
                            taskUseCase.deleteAllScheduledCompletedTasks
                        }
                        ScheduledTaskDeleteOption.ALL_SCHEDULED_COMPLETED_FOR_SELECTED_DAY -> {
                            taskUseCase.deleteScheduledCompletedTasksForSelectedDateUseCase
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
        }
    }


    private fun sendToUi(event: ScheduledTaskListUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun changeHeaderVisibility(isVisible: Boolean) {
        completedTasksHeaderState.value = completedTasksHeaderState.value.copy(
            isVisible = isVisible
        )
    }

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