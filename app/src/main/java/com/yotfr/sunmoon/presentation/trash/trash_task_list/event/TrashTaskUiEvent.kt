package com.yotfr.sunmoon.presentation.trash.trash_task_list.event

import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedTaskListModel

sealed interface TrashTaskUiEvent {

    data class ShowUndoDeleteSnackbar(val task: TrashedTaskListModel) : TrashTaskUiEvent

    data class ShowDateTimeChangeDialog(val task: TrashedTaskListModel): TrashTaskUiEvent

    data class ShowRestoreSnackbar(val task: TrashedTaskListModel) : TrashTaskUiEvent

}