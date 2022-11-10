package com.yotfr.sunmoon.presentation.task.task_details.event

import com.yotfr.sunmoon.presentation.task.task_details.model.SubTaskModel

sealed interface TaskDetailsUiEvent {

    data class ShowUndoDeleteSubTask(val subTask: SubTaskModel) : TaskDetailsUiEvent

    data class ShowDatePicker(val selectionDate: Long?) : TaskDetailsUiEvent

    data class ShowTimePicker(val selectionTime: Long?) : TaskDetailsUiEvent

    data class NavigateToAddSubTask(val taskId: Long) : TaskDetailsUiEvent

    data class SetAlarm(
        val alarmTime: Long,
        val taskId: Long,
        val taskDescription: String,
        val destination:Int,
        val isNewAlarm: Boolean
    ) : TaskDetailsUiEvent

    data class ClearAlarm(
        val taskId: Long
    ): TaskDetailsUiEvent

    object PopBackStack : TaskDetailsUiEvent

}