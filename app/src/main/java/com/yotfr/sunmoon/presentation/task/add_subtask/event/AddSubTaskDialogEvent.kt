package com.yotfr.sunmoon.presentation.task.add_subtask.event


sealed interface AddSubTaskDialogEvent {
    data class AddSubTask(val subTaskText:String):AddSubTaskDialogEvent
}