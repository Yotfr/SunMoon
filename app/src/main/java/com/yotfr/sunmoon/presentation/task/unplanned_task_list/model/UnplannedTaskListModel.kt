package com.yotfr.sunmoon.presentation.task.unplanned_task_list.model

data class UnplannedTaskListModel(
    val taskId:Long,
    val taskDescription:String,
    val importance:Boolean,
    val isCompleted:Boolean,
    val isTrashed:Boolean,
    val scheduledDate:Long?,
    val scheduledTime:Long?,
    val isAddTimeButtonVisible:Boolean,
    val completionProgress:Int,
    val remindDate:Long?,
    val remindTime:Long?,
    val remindDelayTime:Long?
)
