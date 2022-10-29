package com.yotfr.sunmoon.data.repository

import com.yotfr.sunmoon.data.data_source.SubTaskDao
import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository

class SubTaskRepositoryImpl(
    private val subTaskDao: SubTaskDao
): SubTaskRepository {

    override suspend fun createSubTask(subTask: SubTaskEntity) {
        subTaskDao.insertSubTask(subTask)
    }

    override suspend fun deleteSubTask(subTask: SubTaskEntity) {
        subTaskDao.deleteSubTask(subTask)
    }
}