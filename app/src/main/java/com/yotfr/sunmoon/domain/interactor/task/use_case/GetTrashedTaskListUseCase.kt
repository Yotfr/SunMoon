package com.yotfr.sunmoon.domain.interactor.task.use_case

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.domain.model.task.TaskMapper
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetTrashedTaskListUseCase(
    private val taskRepository: TaskRepository
) {
    private val taskWithSubTasksMapper = TaskMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        searchQuery: MutableStateFlow<String>
    ): Flow<Pair<List<Task>, List<Task>>> = withContext(Dispatchers.IO) {
        val uncompletedTrashedTasks = searchQuery.flatMapLatest {
            taskRepository.getTrashedTasks(
                searchQuery = it
            ).map { tasks ->
                taskWithSubTasksMapper.mapFromEntityList(
                    tasks
                )
            }
        }
        val completedTrashedTasks = searchQuery.flatMapLatest {
            taskRepository.getTrashedCompletedTasks(
                searchQuery = it
            ).map { tasks ->
                taskWithSubTasksMapper.mapFromEntityList(
                    tasks
                )
            }
        }
        combine(
            uncompletedTrashedTasks,
            completedTrashedTasks
        ) { uncompletedTrashedTasksOut, completedTrashedTasksOut ->
            Pair(uncompletedTrashedTasksOut, completedTrashedTasksOut)
        }
    }
}
