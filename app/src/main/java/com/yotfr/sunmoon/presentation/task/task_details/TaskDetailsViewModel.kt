package com.yotfr.sunmoon.presentation.task.task_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.task.TaskUseCase
import com.yotfr.sunmoon.domain.repository.sharedpreference.PreferencesHelper
import com.yotfr.sunmoon.presentation.task.task_details.event.TaskDetailsEvent
import com.yotfr.sunmoon.presentation.task.task_details.event.TaskDetailsUiEvent
import com.yotfr.sunmoon.presentation.task.task_details.mapper.SubTaskMapper
import com.yotfr.sunmoon.presentation.task.task_details.mapper.TaskDetailsMapper
import com.yotfr.sunmoon.presentation.task.task_details.model.State
import com.yotfr.sunmoon.presentation.task.task_details.model.TaskDetailsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    state: SavedStateHandle,
    private val taskUseCase: TaskUseCase,
    preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val taskId = state.get<Long>("taskId")
    private val destination = state.get<Int>("destination")

    private val taskDetailsMapper = TaskDetailsMapper()
    private val subTaskMapper = SubTaskMapper()

    val dateFormat = MutableStateFlow("dd/MM/yyyy")

    //TODO:Change shared prefs to dataStore
    private val _sdfPattern = MutableStateFlow(preferencesHelper.getTimeFormat())
    val sdfPattern = _sdfPattern.asStateFlow()

    private val _state = MutableStateFlow(State.SCHEDULED)
    val state = _state.asStateFlow()

    private val changedEtText = MutableStateFlow("")

    private val _rvClickAllowed = MutableStateFlow(false)
    val rvClickAllowed = _rvClickAllowed.asStateFlow()

    private val _uiState = MutableStateFlow(TaskDetailsModel())
    val uiState = _uiState.asSharedFlow()

    private val _uiEvents = Channel<TaskDetailsUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        dateFormat.value = preferencesHelper.getDateFormat() ?: "dd/MM/yyyy"
        val stateDestination = when (destination) {
            TaskDetailsFragment.FROM_SCHEDULED -> State.SCHEDULED
            TaskDetailsFragment.FROM_UNPLANNED -> State.UNPLANNED
            TaskDetailsFragment.FROM_OUTDATED -> State.OUTDATED
            else -> throw IllegalArgumentException("unknown state")
        }
        _state.value = stateDestination
        viewModelScope.launch {
            taskUseCase.getTaskById(
                taskId = taskId ?: throw IllegalArgumentException("can't find taskId")
            ).collect { task ->
                task?.let {
                    _uiState.value = taskDetailsMapper.fromDomain(it)
                        .copy(state = _state.value)
                    _rvClickAllowed.value = if (task.isCompleted) {
                        false
                    }else _state.value != State.OUTDATED
                }
            }
        }
    }


    fun onEvent(event: TaskDetailsEvent) {
        when (event) {
            is TaskDetailsEvent.SaveTask -> {
                viewModelScope.launch {
                    taskUseCase.addTask(
                        taskDetailsMapper.toDomain(
                            _uiState.value.copy(
                                taskDescription = event.taskDescription
                            )
                        )
                    )
                }
            }
            is TaskDetailsEvent.ChangeSubTaskCompletionStatus -> {
                viewModelScope.launch {
                    taskUseCase.changeSubTaskCompletionState(
                        subTaskMapper.toDomain(
                            event.subTask
                        )
                    )
                }
            }
            is TaskDetailsEvent.DeleteTask -> {
                viewModelScope.launch {
                    taskUseCase.trashUntrashTask(
                        taskDetailsMapper.toDomain(
                            _uiState.value
                        )
                    )
                    sendUiEvents(TaskDetailsUiEvent.PopBackStack)
                }
            }
            is TaskDetailsEvent.GetDateForDatePicker -> {
                sendUiEvents(
                    TaskDetailsUiEvent.ShowDatePicker(
                        selectionDate = _uiState.value.scheduledDate
                    )
                )
            }
            is TaskDetailsEvent.GetTimeForTimePicker -> {
                sendUiEvents(
                    TaskDetailsUiEvent.ShowTimePicker(
                        selectionTime = _uiState.value.scheduledTime
                    )
                )
            }
            is TaskDetailsEvent.ChangeTaskScheduledDate -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskScheduledDate(
                        task = taskDetailsMapper.toDomain(
                            _uiState.value
                        ),
                        newDate = event.date
                    )
                }
            }
            is TaskDetailsEvent.ChangeTaskScheduledTime -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskScheduledTime(
                        task = taskDetailsMapper.toDomain(
                            _uiState.value
                        ),
                        newTime = event.time
                    )
                }
            }
            is TaskDetailsEvent.ChangeSubTaskText -> {
                viewModelScope.launch {
                    taskUseCase.changeSubTaskText(
                        subTask = subTaskMapper.toDomain(
                            event.subTask
                        ),
                        newText = event.newText
                    )
                }
            }
            is TaskDetailsEvent.DeleteSubTask -> {
                viewModelScope.launch {
                    taskUseCase.deleteSubTask(
                        subTaskMapper.toDomain(
                            event.subTask
                        )
                    )
                }
                sendUiEvents(
                    TaskDetailsUiEvent.ShowUndoDeleteSubTask(
                        event.subTask
                    )
                )
            }
            is TaskDetailsEvent.ClearDateTime -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskScheduledDateTime(
                        task = taskDetailsMapper.toDomain(
                            _uiState.value
                        ),
                        newDate = null,
                        newTime = null
                    )
                }
                _state.value = State.UNPLANNED
            }
            is TaskDetailsEvent.UndoDeleteSubTask -> {
                viewModelScope.launch {
                    taskUseCase.addSubTask(
                        subTaskMapper.toDomain(
                            event.subTask
                        )
                    )
                }
            }
            is TaskDetailsEvent.ChangeTaskScheduledDateTime -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskScheduledDateTime(
                        task = taskDetailsMapper.toDomain(
                            _uiState.value
                        ),
                        newDate = event.date,
                        newTime = event.time
                    )
                }
                _state.value = State.SCHEDULED
            }
            TaskDetailsEvent.GetTaskId -> {
                sendUiEvents(
                    TaskDetailsUiEvent.NavigateToAddSubTask(
                        taskId = _uiState.value.taskId!!
                    )
                )
            }
            TaskDetailsEvent.MarkUndone -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskCompletionState(
                        task = taskDetailsMapper.toDomain(
                            _uiState.value
                        )
                    )
                }
            }
            is TaskDetailsEvent.ChangeTaskText -> {
                changedEtText.value = event.newText
            }
            is TaskDetailsEvent.ApplyTaskTextChange -> {
                if (changedEtText.value.isNotEmpty() && changedEtText.value.isNotBlank()) {
                    viewModelScope.launch {
                        taskUseCase.addTask(
                            task = taskDetailsMapper.toDomain(
                                _uiState.value.copy(
                                    taskDescription = changedEtText.value
                                )
                            )
                        )
                    }
                }
            }
            is TaskDetailsEvent.SetTaskRemindDateTime -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskRemindDateTime(
                        task = taskDetailsMapper.toDomain(
                            _uiState.value.copy(
                                taskDescription = changedEtText.value
                            )
                        ),
                        newDate = event.remindDate,
                        newTime = event.remindTime,
                        remindDelayTime = event.remindInMillis
                    )
                }
                sendUiEvents(TaskDetailsUiEvent.SetAlarm(
                    alarmTime = event.remindInMillis,
                    taskId = _uiState.value.taskId!!,
                    taskDescription = _uiState.value.taskDescription,
                    isNewAlarm = true
                ))
            }
            is TaskDetailsEvent.ChangeTaskRemindDateTime -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskRemindDateTime(
                        task = taskDetailsMapper.toDomain(
                            _uiState.value.copy(
                                taskDescription = changedEtText.value
                            )
                        ),
                        newDate = event.remindDate,
                        newTime = event.remindTime,
                        remindDelayTime = event.remindInMillis
                    )
                }
                sendUiEvents(TaskDetailsUiEvent.SetAlarm(
                    alarmTime = event.remindInMillis,
                    taskId = _uiState.value.taskId!!,
                    taskDescription = _uiState.value.taskDescription,
                    isNewAlarm = false
                ))
            }
            TaskDetailsEvent.ClearTaskRemindDateTime -> {
                viewModelScope.launch {
                    taskUseCase.changeTaskRemindDateTime(
                        task = taskDetailsMapper.toDomain(
                            _uiState.value.copy(
                                taskDescription = changedEtText.value
                            )
                        ),
                        newDate = null,
                        newTime = null,
                        remindDelayTime = null
                    )
                }
                sendUiEvents(TaskDetailsUiEvent.ClearAlarm(
                    taskId = _uiState.value.taskId!!
                ))
            }
        }
    }

    private fun sendUiEvents(event: TaskDetailsUiEvent) {
        viewModelScope.launch {
            _uiEvents.send(event)
        }
    }


}