package com.yotfr.sunmoon.domain.repository.task

import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity

interface SubTaskRepository {
    suspend fun createSubTask(subTask: SubTaskEntity)
    suspend fun deleteSubTask(subTask: SubTaskEntity)
    suspend fun deleteAllRelatedSubTasks(taskId: Long)
}
