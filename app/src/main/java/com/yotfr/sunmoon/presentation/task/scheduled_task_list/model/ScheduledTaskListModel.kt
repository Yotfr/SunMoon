package com.yotfr.sunmoon.presentation.task.scheduled_task_list.model

data class ScheduledTaskListModel(
    val taskId:Long,
    val taskDescription:String,
    val isCompleted:Boolean,
    val isTrashed:Boolean,
    val scheduledDate:Long?,
    val scheduledTime:Long?,
    val scheduledFormattedTime:String,
    val isAddTimeButtonVisible:Boolean,
    val completionProgress:Int,
    val remindDate:Long?,
    val remindTime:Long?,
    val remindDelayTime:Long?,
    val importance:Boolean
)