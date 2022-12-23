package com.yotfr.sunmoon.presentation.task.task_details.model

data class DateTimeSettings(
    val datePattern: String = "yyyy/MM/dd",
    val timePattern: String = "HH:mm",
    val timeFormat: Int = 2
)
