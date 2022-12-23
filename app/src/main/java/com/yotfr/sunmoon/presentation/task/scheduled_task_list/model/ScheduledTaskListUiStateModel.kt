package com.yotfr.sunmoon.presentation.task.scheduled_task_list.model

data class ScheduledTaskListUiStateModel(
    val uncompletedTasks: List<ScheduledTaskListModel>,
    val completedTasks: List<ScheduledTaskListModel>,
    val headerState: ScheduledCompletedHeaderStateModel = ScheduledCompletedHeaderStateModel(),
    val footerState: ScheduledFooterModel = ScheduledFooterModel()
)
