package com.yotfr.sunmoon.domain.interactor.task

import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteUnplanned(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            taskRepository.deleteAllUnplannedTasks()
        }
    }
}