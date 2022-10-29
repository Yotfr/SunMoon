package com.yotfr.sunmoon.presentation.task.add_subtask.model

data class AddSubTaskDialogModel(
    val subTaskId:Long? = null,
    val subTaskDescription:String,
    val completionStatus:Boolean = false,
    val taskId:Long
)