package com.yotfr.sunmoon.presentation.task.unplanned_task_list.event

import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListModel

sealed interface UnplannedTaskListUiEvent {
    data class UndoDeleteUnplannedTask(val task: UnplannedTaskListModel): UnplannedTaskListUiEvent
    data class NavigateToScheduledTask(val taskDate: Long): UnplannedTaskListUiEvent
}