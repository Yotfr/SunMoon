package com.yotfr.sunmoon.presentation.task.add_task.model

data class AddTaskUiState(
    val taskId:Long? = null,
    val taskDescription:String? = "",
    val selectedDate: Long? = null,
    val selectedTime: Long? = null
)