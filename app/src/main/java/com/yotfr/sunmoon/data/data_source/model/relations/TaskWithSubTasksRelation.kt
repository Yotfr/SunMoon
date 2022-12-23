package com.yotfr.sunmoon.data.data_source.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity
import com.yotfr.sunmoon.data.data_source.model.task.TaskEntity

data class TaskWithSubTasksRelation(
    @Embedded val taskEntity: TaskEntity,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val nestedToDo: List<SubTaskEntity>
)
