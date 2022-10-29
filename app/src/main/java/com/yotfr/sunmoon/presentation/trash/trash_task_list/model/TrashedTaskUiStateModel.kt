package com.yotfr.sunmoon.presentation.trash.trash_task_list.model

data class TrashedTaskUiStateModel(
    val uncompletedTasks: List<TrashedTaskListModel>,
    val completedTasks:List<TrashedTaskListModel>,
    val headerState: TrashedCompletedHeaderStateModel
)