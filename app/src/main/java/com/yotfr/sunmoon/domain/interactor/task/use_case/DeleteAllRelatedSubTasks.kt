package com.yotfr.sunmoon.domain.interactor.task.use_case

import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAllRelatedSubTasks(
    private val subTaskRepository: SubTaskRepository
) {
    suspend operator fun invoke(taskId:Long) {
        withContext(Dispatchers.IO) {
            subTaskRepository.deleteAllRelatedSubTasks(
                taskId = taskId
            )
        }
    }
}