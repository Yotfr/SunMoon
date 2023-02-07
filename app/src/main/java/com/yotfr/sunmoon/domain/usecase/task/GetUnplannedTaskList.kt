package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetUnplannedTaskList(
    private val taskRepository: TaskRepository
) {
    private val taskMapper = TaskMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(searchQuery: MutableStateFlow<String>): Flow<Pair<List<Task>, List<Task>>> =
        withContext(Dispatchers.IO) {
            searchQuery.flatMapLatest { searchQuery ->
                val uncompletedTasks = taskRepository.getUnplannedTasks(
                    searchQuery = searchQuery
                ).map { taskMapper.mapFromEntityList(it) }
                val completedTasks = taskRepository.getUnplannedCompletedTasks(
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
