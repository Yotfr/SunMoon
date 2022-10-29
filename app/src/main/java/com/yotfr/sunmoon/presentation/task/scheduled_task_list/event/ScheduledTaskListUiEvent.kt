package com.yotfr.sunmoon.presentation.task.scheduled_task_list.event

import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel

sealed interface ScheduledTaskListUiEvent {
    data class UndoDeleteScheduledTask(val task: ScheduledTaskListModel) : ScheduledTaskListUiEvent
}