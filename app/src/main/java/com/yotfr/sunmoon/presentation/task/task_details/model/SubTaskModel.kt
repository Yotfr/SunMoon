package com.yotfr.sunmoon.presentation.task.task_details.model

data class SubTaskModel(
    val subTaskId: Long? = null,
    val subTaskDescription: String,
    val completionStatus: Boolean ? = false,
    val taskId: Long,
    val isEnabled: Boolean
)
