package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAllOutdatedCompletedTasks(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(expDate: Long) {
        withContext(Dispatchers.IO) {
            taskRepository.deleteAllOutdatedCompletedTasks(expDate)
        }
    }
}
