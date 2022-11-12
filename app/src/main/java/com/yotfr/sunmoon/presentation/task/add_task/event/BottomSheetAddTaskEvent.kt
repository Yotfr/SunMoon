package com.yotfr.sunmoon.presentation.task.add_task.event


sealed interface BottomSheetAddTaskEvent {

    data class AddTask(val taskDescription: String) : BottomSheetAddTaskEvent

    data class ChangeTime(val newTime: Long?) : BottomSheetAddTaskEvent

    data class ChangeDate(
        val newDate: Long?
    ) : BottomSheetAddTaskEvent


    object ClearDateTime : BottomSheetAddTaskEvent

    object NavigateToDateSelector : BottomSheetAddTaskEvent

}