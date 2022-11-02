package com.yotfr.sunmoon.presentation.trash.trash_task_list.event

import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedTaskListModel

sealed interface TrashTaskEvent {

    data class RestoreTrashedTask(val task: TrashedTaskListModel) : TrashTaskEvent

    data class RestoreTrashedTaskWithDateTimeChanged(
        val task: TrashedTaskListModel,
        val date: Long?,
        val time: Long?
    ) : TrashTaskEvent

    data class DeleteTrashedTask(val task: TrashedTaskListModel) : TrashTaskEvent

    data class UpdateSearchQuery(val searchQuery: String) : TrashTaskEvent

    data class UndoDeleteTrashedTask(val task: TrashedTaskListModel) : TrashTaskEvent

    object DeleteAllTrashedTask : TrashTaskEvent

    object DeleteAllTrashedCompletedTask : TrashTaskEvent

    object ChangeCompletedTasksVisibility : TrashTaskEvent
}