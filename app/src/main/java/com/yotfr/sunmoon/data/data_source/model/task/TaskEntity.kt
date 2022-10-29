package com.yotfr.sunmoon.data.data_source.model.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val taskId:Long?,
    val importance:Boolean,
    val taskDescription:String,
    val isCompleted:Boolean,
    val isTrashed:Boolean,
    val scheduledDate:Long?,
    val scheduledTime:Long?,
    val remindDate:Long?,
    val remindTime:Long?,
    val remindDelayTime:Long?
):Serializable