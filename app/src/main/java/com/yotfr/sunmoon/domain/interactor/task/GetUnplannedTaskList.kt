package com.yotfr.sunmoon.domain.interactor.task

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetUnplannedTaskList(
    private val taskRepository: TaskRepository,
) {
    private val taskMapper = TaskMapper()

    suspend operator fun invoke(searchQuery: MutableStateFlow<String>): Flow<Pair<List<Task>, List<Task>>> =
        withContext(Dispatchers.IO) {
            searchQuery.flatMapLatest { searchQuery ->
                val uncompletedTasks = taskRepository.getUnplannedUserTasks(
                    searchQuery = searchQuery
                ).map { taskMapper.mapFromEntityList(it) }
                val completedTasks = taskRepository.getUnplannedCompletedUserTasks(
                    searchQuery = searchQuery
                ).map { taskMapper.mapFromEntityList(it) }
                combine(
                    uncompletedTasks,
                    completedTasks
                ) { uncompleted, completed ->
                    Pair(uncompleted, completed)
                }
            }
        }
}

