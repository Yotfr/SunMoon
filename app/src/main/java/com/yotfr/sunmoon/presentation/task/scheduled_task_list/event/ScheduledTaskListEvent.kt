package com.yotfr.sunmoon.presentation.task.scheduled_task_list.event

import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskDeleteOption
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel

sealed interface ScheduledTaskListEvent {

    data class UpdateSearchQuery(val searchQuery: String) : ScheduledTaskListEvent

    data class ChangeTaskScheduledTime(val task: ScheduledTaskListModel, val newTime: Long) :
        ScheduledTaskListEvent

    data class ChangeTaskCompletionStatus(
        val task: ScheduledTaskListModel
    ) : ScheduledTaskListEvent

    data class ChangeScheduledTaskImportance(
        val task: ScheduledTaskListModel
    ):ScheduledTaskListEvent

    data class ChangeSelectedDate(val newSelectedDate: Long) : ScheduledTaskListEvent

    data class TrashScheduledTask(val task: ScheduledTaskListModel) : ScheduledTaskListEvent

    data class UndoTrashItem(val task: ScheduledTaskListModel) : ScheduledTaskListEvent

    data class DeleteTasks(val deleteOption: ScheduledTaskDeleteOption) : ScheduledTaskListEvent

    data class SelectCalendarDate(val selectedDate:Long) : ScheduledTaskListEvent

    object ChangeCompletedTasksVisibility : ScheduledTaskListEvent

}