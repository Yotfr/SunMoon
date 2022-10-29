package com.yotfr.sunmoon.domain.interactor.task

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetOutdatedTaskList(
    private val taskRepository: TaskRepository,
) {
    private val taskMapper = TaskMapper()

    suspend operator fun invoke(
        searchQuery: MutableStateFlow<String>,
        currentDate: Long
    ): Flow<Pair<List<Task>, List<Task>>> = withContext(Dispatchers.IO) {
        searchQuery.flatMapLatest { searchQuery ->
            val uncompletedTasks = taskRepository.getOutdatedUserTasks(
                searchQuery,
                currentDate
            ).map {
                taskMapper.mapFromEntityList(it)
            }
            val completedTasks = taskRepository.getOutdatedCompletedUserTasks(
                searchQuery,
                currentDate
            ).map {
                taskMapper.mapFromEntityList(it)
            }
            combine(
                uncompletedTasks,
                completedTasks
            ) { uncompletedTasksOut, completedTasksOut ->
                Pair(uncompletedTasksOut, completedTasksOut)
            }
        }
    }
}