package com.yotfr.sunmoon.data.datasource.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.yotfr.sunmoon.data.datasource.entity.task.SubTaskEntity
import com.yotfr.sunmoon.data.datasource.entity.task.TaskEntity

data class TaskWithSubTasksRelation(
    @Embedded val taskEntity: TaskEntity,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val nestedToDo: List<SubTaskEntity>
)
