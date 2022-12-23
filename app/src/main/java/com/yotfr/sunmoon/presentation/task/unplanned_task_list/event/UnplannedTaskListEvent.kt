package com.yotfr.sunmoon.presentation.task.unplanned_task_list.event

import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedDeleteOption
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListModel

sealed interface UnplannedTaskListEvent {

    data class UpdateSearchQuery(val searchQuery: String) : UnplannedTaskListEvent

    data class ChangeUnplannedTaskCompletionStatus(
        val task: UnplannedTaskListModel
    ) : UnplannedTaskListEvent

    data class ScheduleTask(
        val task: UnplannedTaskListModel,
        val selectedDate: Long,
        val selectedTime: Long?
    ) : UnplannedTaskListEvent

    data class TrashUnplannedTask(val task: UnplannedTaskListModel) : UnplannedTaskListEvent

    data class UndoTrashItem(val task: UnplannedTaskListModel) : UnplannedTaskListEvent

    data class DeleteTasks(val deleteOption: UnplannedDeleteOption) : UnplannedTaskListEvent

    data class ChangeUnplannedTaskImportance(
        val task: UnplannedTaskListModel
    ) : UnplannedTaskListEvent

    object ChangeCompletedTasksVisibility : UnplannedTaskListEvent
}
