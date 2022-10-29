package com.yotfr.sunmoon.data.data_source.model.task

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "subTask")
data class SubTaskEntity (
    @PrimaryKey(autoGenerate = true) val subTaskId:Long?,
    val subTaskDescription:String,
    val completionStatus:Boolean,
    val taskId:Long
        )

