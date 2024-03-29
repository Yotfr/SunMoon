package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.model.task.SubTask
import com.yotfr.sunmoon.domain.model.task.SubTaskMapper
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteSubTask(
    private val subTaskRepository: SubTaskRepository
) {
    private val suBTaskMapper = SubTaskMapper()

    suspend operator fun invoke(subTask: SubTask) = withContext(Dispatchers.IO) {
        subTaskRepository.deleteSubTask(
            suBTaskMapper.mapToEntity(
                subTask
            )
        )
    }
}
