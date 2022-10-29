package com.yotfr.sunmoon.presentation.trash.trash_task_list.model

data class TrashedTaskListModel(
    val taskId:Long,
    val taskDescription:String,
    val completionStatus:Boolean,
    val isTrashed:Boolean,
    val scheduledDate:Long?,
    val scheduledTime:Long?,
    val scheduledTimeString:String,
    val completionProgress:Int,
    val isAddTimeButtonVisible:Boolean,
    val remindDate:Long?,
    val remindTime:Long?,
    val remindDelayTime:Long?,
    val importance:Boolean
)