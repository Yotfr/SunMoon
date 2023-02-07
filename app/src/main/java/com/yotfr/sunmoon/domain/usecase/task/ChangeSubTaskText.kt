package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.model.task.SubTask
import com.yotfr.sunmoon.domain.model.task.SubTaskMapper
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeSubTaskText(
    private val subTaskRepository: SubTaskRepository
) {
    private val subTaskMapper = SubTaskMapper()

    suspend operator fun invoke(subTask: SubTask, newText: String) {
        withContext(Dispatchers.IO) {
            subTaskRepository.createSubTask(
                subTaskMapper.mapToEntity(subTask).copy(
                    subTaskDescription = newText
                )
            )
        }
    }
}
