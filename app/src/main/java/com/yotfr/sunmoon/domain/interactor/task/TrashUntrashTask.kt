package com.yotfr.sunmoon.domain.interactor.task

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrashUntrashTask(
    private val taskRepository: TaskRepository
) {
    private val taskMapper = TaskMapper()
    suspend operator fun invoke(task: Task) {
        withContext(Dispatchers.IO) {
            taskRepository.insertTask(
                taskMapper.mapToEntity(task).copy(
                    isTrashed = !task.isTrashed
                )
            )
        }
    }
}