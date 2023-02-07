package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeTaskRemindDateTime(
    private val taskRepository: TaskRepository
) {
    private val taskMapper = TaskMapper()

    suspend operator fun invoke(
        task: Task,
        newDate: Long?,
        newTime: Long?,
        remindDelayTime: Long?
    ) {
        withContext(Dispatchers.IO) {
            taskRepository.upsertTask(
                taskMapper.mapToEntity(task).copy(
                    remindDate = newDate,
                    remindTime = newTime,
                    remindDelayTime = remindDelayTime
                )
            )
        }
    }
}