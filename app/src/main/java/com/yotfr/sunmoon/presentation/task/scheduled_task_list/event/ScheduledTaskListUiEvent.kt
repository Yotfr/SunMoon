package com.yotfr.sunmoon.presentation.task.scheduled_task_list.event

import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel

sealed interface ScheduledTaskListUiEvent {
    data class UndoTrashScheduledTask(val task: ScheduledTaskListModel) : ScheduledTaskListUiEvent
    data class SelectCalendarDate(val selectedDate: Long) : ScheduledTaskListUiEvent
}
