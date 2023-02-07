package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetTaskById(
    private val taskRepository: TaskRepository
) {

    private val taskWithSubTasksMapper = TaskMapper()

    suspend operator fun invoke(taskId: Long): Flow<Task?> =
        withContext(Dispatchers.IO) {
            taskRepository.getTaskById(taskId).map {
                it?.let {
                    taskWithSubTasksMapper.mapFromEntity(it)
                }
            }
        }
}