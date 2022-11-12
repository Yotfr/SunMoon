package com.yotfr.sunmoon.presentation.task.task_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.task.TaskUseCase
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.task.task_details.event.TaskDetailsEvent
import com.yotfr.sunmoon.presentation.task.task_details.event.TaskDetailsUiEvent
import com.yotfr.sunmoon.presentation.task.task_details.mapper.SubTaskMapper
import com.yotfr.sunmoon.presentation.task.task_details.mapper.TaskDetailsMapper
import com.yotfr.sunmoon.presentation.task.task_details.model.DateTimeSettings
import com.yotfr.sunmoon.presentation.task.task_details.model.State
import com.yotfr.sunmoon.presentation.task.task_details.model.TaskDetailsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    state: SavedStateHandle,
    private val taskUseCase: TaskUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val taskDetailsMapper = TaskDetailsMapper()
    private val subTaskMapper = SubTaskMapper()

    //task id coming from task list
    private val taskId = state.get<Long>("taskId")

    //from where navigated
    private val destination = state.get<Int>("destination")

    //date&time formats and patterns from dataStore
    private val _dateTimeSettings = MutableStateFlow(DateTimeSettings())
    val dateTimeSettings = _dateTimeSettings.asStateFlow()

    //state for destination
    private val _state = MutableStateFlow(State.SCHEDULED)
    val state = _state.asStateFlow()

    //state for task description edit text (value of this state will be saved after editText will lose focus
    private val changedEtText = MutableStateFlow("")

    private val _uiState = MutableStateFlow(TaskDetailsModel())
    val uiState = _uiState.asStateFlow()

    //channel for uiEvents
    private val _uiEvents = Channel<TaskDetailsUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        //parse destination
        val stateDestination = when (destination) {
            TaskDetailsFragment.FROM_SCHEDULED -> State.SCHEDULED
            TaskDetailsFragment.FROM_UNPLANNED -> State.UNPLANNED
            TaskDetailsFragment.FROM_OUTDATED -> State.OUTDATED
            else -> throw IllegalArgumentException("unknown state")
        }
        _state.value = stateDestination
        //get task
        viewModelScope.launch {
            taskUseCase.getTaskById(
                taskId = taskId ?: throw IllegalArgumentException("can't find taskId")
            ).collect { task ->
                task?.let {
                    _uiState.value = taskDetailsMapper.fromDomain(it)
                        .copy(state = _state.value)
                }
            }
        }
        //get date&time patterns and formats from dataStore
        viewModelScope.launch {
            dataStoreRepository.getDateTimeSettings().collect{
                _dateTimeSettings.value = DateTimeSettings(
                    datePattern = it.first ?: "yyyy/MM/dd" ,
                    timePattern = it.second,
                    timeFormat = it.third ?: 2
                )
            }
        }
    }

    //method for fragment to communicate with viewModel
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
                if (!_uiState.value.completionStatus){
                    sendUiEvents(
                        TaskDetailsUiEvent.NavigateToAddSubTask(
                            taskId = _uiState.value.taskId!!
                        )
                    )
                }
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
                    destination = destination ?: throw IllegalArgumentException(
                        "not found destination"
                    ),
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
                    destination = destination ?: throw IllegalArgumentException(
                        "not found destination"
                    ),
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

    //send uiEvents to uiEvent channel
    private fun sendUiEvents(event: TaskDetailsUiEvent) {
        viewModelScope.launch {
            _uiEvents.send(event)
        }
    }
}