package com.yotfr.sunmoon.domain.interactor.task.use_case

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeTaskCompletionState(
    private val taskRepository: TaskRepository
) {
    private val taskMapper = TaskMapper()

    //complete/undone task
    suspend operator fun invoke(task: Task) {
        withContext(Dispatchers.IO) {
            taskRepository.upsertTask(
                taskMapper.mapToEntity(task).copy(
                    isCompleted = !task.isCompleted
                )
            )
        }
    }
}