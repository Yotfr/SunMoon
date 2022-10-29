package com.yotfr.sunmoon.presentation.task.task_details.model


data class TaskDetailsModel(
    val taskId:Long ?= null,
    val importance:Boolean = false,
    val taskDescription:String = "",
    val completionStatus:Boolean = false,
    val isTrashed:Boolean = false,
    val scheduledDate:Long? = null,
    val scheduledTime:Long? = null,
    val subTasks:List<SubTaskModel> = emptyList(),
    val completionProgress:Int = 0,
    val state:State = State.SCHEDULED,
    val remindDate:Long? = null,
    val remindTime:Long? = null,
    val remindDelayTime:Long? = null
)