package com.yotfr.sunmoon.domain.interactor.task.use_case

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteTaskUseCase (
    private val taskRepository:TaskRepository
){
    private val taskMapper = TaskMapper()

    suspend operator fun invoke(task:Task){
        withContext(Dispatchers.IO){
            taskRepository.deleteTask(taskMapper.mapToEntity(task))
        }
    }
}