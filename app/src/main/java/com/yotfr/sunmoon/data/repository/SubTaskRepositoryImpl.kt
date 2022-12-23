package com.yotfr.sunmoon.data.repository

import com.yotfr.sunmoon.data.data_source.SubTaskDao
import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubTaskRepositoryImpl @Inject constructor(
    private val subTaskDao: SubTaskDao
) : SubTaskRepository {

    override suspend fun createSubTask(subTask: SubTaskEntity) {
        subTaskDao.upsertSubTask(subTask)
    }

    override suspend fun deleteSubTask(subTask: SubTaskEntity) {
        subTaskDao.deleteSubTask(subTask)
    }

    override suspend fun deleteAllRelatedSubTasks(taskId: Long) {
        subTaskDao.deleteAllRelatedSubTasks(taskId)
    }
}
