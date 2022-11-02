package com.yotfr.sunmoon.presentation.task.unplanned_task_list.model

data class UnplannedTaskListUiStateModel(
    val uncompletedTasks:List<UnplannedTaskListModel>,
    val completedTasks:List<UnplannedTaskListModel>,
    val headerState: UnplannedCompletedHeaderStateModel,
    val footerState:UnplannedFooterModel
)