package com.yotfr.sunmoon.presentation.task.outdated_task_list.event

import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedDeleteOption
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListModel

sealed interface OutdatedTaskEvent {

    data class UpdateSearchQuery(val searchQuery: String): OutdatedTaskEvent

    data class ScheduleTask(val task:OutdatedTaskListModel, val date:Long?, val time:Long?,
    val isMakeUndoneNeeded:Boolean = false): OutdatedTaskEvent

    data class TrashOutdatedTask(val task: OutdatedTaskListModel): OutdatedTaskEvent

    data class UndoDeleteOutdatedTask(val task: OutdatedTaskListModel): OutdatedTaskEvent

    data class DeleteTasks(val deleteOption: OutdatedDeleteOption):OutdatedTaskEvent

    object ChangeCompletedTasksVisibility: OutdatedTaskEvent

}