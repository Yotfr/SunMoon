package com.yotfr.sunmoon.presentation.task.outdated_task_list.event

import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListModel

sealed interface OutdatedTaskUiEvent {
    data class UndoDeleteOutdatedTask(val task: OutdatedTaskListModel): OutdatedTaskUiEvent
    data class NavigateToScheduledTask(val taskDate: Long): OutdatedTaskUiEvent
}