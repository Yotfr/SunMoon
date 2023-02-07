package com.yotfr.sunmoon.domain.usecase.task

import com.yotfr.sunmoon.domain.model.task.SubTask
import com.yotfr.sunmoon.domain.model.task.SubTaskMapper
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository

class ChangeSubTaskCompletionState(
    private val subTaskRepository: SubTaskRepository
) {
    private val subTaskMapper = SubTaskMapper()

    // complete/undone subTask
    suspend operator fun invoke(subTask: SubTask) {
        subTaskRepository.createSubTask(subTaskMapper.mapToEntity(subTask.copy(completionStatus = !subTask.completionStatus)))
    }
}
