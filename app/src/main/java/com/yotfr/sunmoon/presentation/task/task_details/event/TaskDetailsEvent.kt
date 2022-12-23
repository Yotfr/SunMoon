package com.yotfr.sunmoon.presentation.task.task_details.event

import com.yotfr.sunmoon.presentation.task.task_details.model.SubTaskModel

sealed interface TaskDetailsEvent {

    data class SaveTask(val taskDescription: String) : TaskDetailsEvent

    data class ChangeSubTaskCompletionStatus(val subTask: SubTaskModel) : TaskDetailsEvent

    data class ChangeSubTaskText(val subTask: SubTaskModel, val newText: String) : TaskDetailsEvent

    data class ChangeTaskText(val newText: String) : TaskDetailsEvent

    data class DeleteSubTask(val subTask: SubTaskModel) : TaskDetailsEvent

    data class ChangeTaskScheduledDate(val date: Long?) : TaskDetailsEvent

    data class ChangeTaskScheduledTime(val time: Long?) : TaskDetailsEvent

    data class UndoDeleteSubTask(val subTask: SubTaskModel) : TaskDetailsEvent

    data class ChangeTaskScheduledDateTime(val date: Long, val time: Long?) : TaskDetailsEvent

    data class ChangeTaskRemindDateTime(
        val remindDate: Long,
        val remindTime: Long,
        val remindInMillis: Long
    ) : TaskDetailsEvent

    data class SetTaskRemindDateTime(
        val remindDate: Long,
        val remindTime: Long,
        val remindInMillis: Long
    ) : TaskDetailsEvent

    object ClearTaskRemindDateTime : TaskDetailsEvent

    object ApplyTaskTextChange : TaskDetailsEvent

    object DeleteTask : TaskDetailsEvent

    object GetTimeForTimePicker : TaskDetailsEvent

    object GetDateForDatePicker : TaskDetailsEvent

    object ClearDateTime : TaskDetailsEvent

    object GetTaskId : TaskDetailsEvent

    object MarkUndone : TaskDetailsEvent
}
