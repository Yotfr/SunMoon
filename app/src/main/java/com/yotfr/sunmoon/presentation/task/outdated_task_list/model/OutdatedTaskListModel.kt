package com.yotfr.sunmoon.presentation.task.outdated_task_list.model

data class OutdatedTaskListModel(
    val taskId:Long,
    val taskDescription:String,
    val isCompleted:Boolean,
    val isTrashed:Boolean,
    val scheduledDate:Long?,
    val scheduledTime:Long?,
    val scheduledFormattedTime:String,
    val formattedOverDueTime:String,
    val completionProgress:Int,
    val isAddTimeButtonVisible:Boolean,
    val remindDate:Long?,
    val remindTime:Long?,
    val remindDelayTime:Long?,
    val importance:Boolean
)