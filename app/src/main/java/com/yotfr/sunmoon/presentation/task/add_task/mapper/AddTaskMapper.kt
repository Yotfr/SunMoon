package com.yotfr.sunmoon.presentation.task.add_task.mapper

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.presentation.task.add_task.model.AddTaskUiState

class AddTaskMapper {

    fun toDomain(addTaskUiState: AddTaskUiState):Task{
        return Task(
            taskId = addTaskUiState.taskId,
            taskDescription = addTaskUiState.taskDescription ?: throw IllegalArgumentException(
                "Not found taskDescription"
            ),
            scheduledDate = addTaskUiState.selectedDate,
            scheduledTime = addTaskUiState.selectedTime,
            isCompleted = false,
            isTrashed = false,
            subTasks = emptyList(),
            remindTime = null,
            remindDate = null,
            remindDelayTime = null,
            importance = false
        )
    }
}