package com.yotfr.sunmoon.domain.interactor.task

import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteScheduledTasksForSelectedDate(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(selectedDate:Long) {
        withContext(Dispatchers.IO) {
            taskRepository.deleteScheduledTasksOfSelectedDay(selectedDate)
        }
    }
}