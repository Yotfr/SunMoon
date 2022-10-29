package com.yotfr.sunmoon.presentation.task.add_task.event


sealed interface BottomSheetAddTaskEvent {

    data class AddTask(val taskDescription: String) : BottomSheetAddTaskEvent

    data class ChangeTime(val newTime: Long?) : BottomSheetAddTaskEvent

    data class DateTimeChanged(val newDate: Long?, val newTime: Long?) : BottomSheetAddTaskEvent

    object ClearDateTime : BottomSheetAddTaskEvent

    object NavigateToDateSelector : BottomSheetAddTaskEvent

}