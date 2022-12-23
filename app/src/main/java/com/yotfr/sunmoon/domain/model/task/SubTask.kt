package com.yotfr.sunmoon.domain.model.task

data class SubTask(
    val subTaskId: Long?,
    val subTaskDescription: String,
    val completionStatus: Boolean,
    val taskId: Long
)
