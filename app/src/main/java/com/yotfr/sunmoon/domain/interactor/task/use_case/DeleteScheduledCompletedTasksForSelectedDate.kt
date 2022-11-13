package com.yotfr.sunmoon.domain.interactor.task.use_case

import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteScheduledCompletedTasksForSelectedDate(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(selectedDate:Long) {
        withContext(Dispatchers.IO) {
            taskRepository.deleteScheduledCompletedTasksOfSelectedDay(selectedDate)
        }
    }
}