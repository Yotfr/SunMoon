package com.yotfr.sunmoon.data.repository

import com.yotfr.sunmoon.data.datasource.SubTaskDao
import com.yotfr.sunmoon.data.datasource.entity.task.SubTaskEntity
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubTaskRepositoryImpl @Inject constructor(
    private val subTaskDao: SubTaskDao
) : SubTaskRepository {

    override suspend fun createSubTask(subTask: SubTaskEntity) {
        subTaskDao.insertSubTask(subTask)
    }

    override suspend fun deleteSubTask(subTask: SubTaskEntity) {
        subTaskDao.deleteSubTask(subTask)
    }

    override suspend fun deleteAllRelatedSubTasks(taskId: Long) {
        subTaskDao.deleteAllRelatedSubTasks(taskId)
    }
}
