package com.yotfr.sunmoon.presentation.task.outdated_task_list.model

data class OutdatedTaskListUiStateModel(
    val uncompletedTasks: List<OutdatedTaskListModel>,
    val completedTasks: List<OutdatedTaskListModel>,
    val headerState: OutdatedCompletedHeaderStateModel,
    val footerState: OutdatedFooterModel
)
