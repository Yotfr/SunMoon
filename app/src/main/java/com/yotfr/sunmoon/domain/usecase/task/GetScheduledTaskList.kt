package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetScheduledTaskList(
    private val taskRepository: TaskRepository
) {
    private val taskWithSubTasksMapper = TaskMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        selectedDate: MutableStateFlow<Long>,
        searchQuery: MutableStateFlow<String>,
        currentDate: Long
    ): Flow<Pair<List<Task>, List<Task>>> = withContext(Dispatchers.IO) {
        val uncompletedTasks = combine(
            selectedDate,
            searchQuery
        ) { selectedDate, searchQuery ->
            Pair(selectedDate, searchQuery)
        }.flatMapLatest { (selectedDate, searchQuery) ->
            taskRepository.getScheduledTasks(
                selectedDate,
                searchQuery,
                currentDate
            ).map {
                taskWithSubTasksMapper.mapFromEntityList(it)
            }
        }
        val completedTasks = combine(
            selectedDate,
            searchQuery
        ) { selectedDate, searchQuery ->
            Pair(selectedDate, searchQuery)
        }.flatMapLatest { (selectedDate, searchQuery) ->
            taskRepository.getScheduledCompletedTasks(
                selectedDate,
                searchQuery,
                currentDate
            ).map {
                taskWithSubTasksMapper.mapFromEntityList(it)
            }
        }
        combine(
            uncompletedTasks,
            completedTasks
        ) { uncompletedTasksOut, completedTasksOut ->
            Pair(uncompletedTasksOut, completedTasksOut)
        }
    }
}
