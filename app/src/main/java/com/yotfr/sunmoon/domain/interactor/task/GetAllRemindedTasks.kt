package com.yotfr.sunmoon.domain.interactor.task

import com.yotfr.sunmoon.data.data_source.model.relations.TaskWithSubTasksRelation
import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAllRemindedTasks(
    private val taskRepository: TaskRepository
) {
    private val taskMapper = TaskMapper()

    suspend operator fun invoke(): List<Task> =
        withContext(Dispatchers.IO) {
            taskRepository.getAllremindedTasks().map {
                taskMapper.mapFromEntity(
                    TaskWithSubTasksRelation(
                        taskEntity = it,
                        nestedToDo = emptyList()
                    )
                )
            }
        }
}
