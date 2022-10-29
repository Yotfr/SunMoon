package com.yotfr.sunmoon.domain.model.task

data class Task(
    val taskId:Long?,
    val importance:Boolean,
    val taskDescription:String,
    val isCompleted:Boolean,
    val isTrashed:Boolean,
    val scheduledDate:Long?,
    val scheduledTime:Long?,
    val remindDate:Long?,
    val remindTime:Long?,
    val subTasks:List<SubTask>,
    val remindDelayTime:Long?
)
