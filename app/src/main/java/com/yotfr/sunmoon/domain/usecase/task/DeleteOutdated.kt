package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteOutdated(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(expDate: Long) {
        withContext(Dispatchers.IO) {
            taskRepository.deleteAllOutdatedTasks(expDate)
        }
    }
}
