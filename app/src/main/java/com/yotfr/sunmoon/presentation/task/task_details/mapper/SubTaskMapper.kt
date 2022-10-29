package com.yotfr.sunmoon.presentation.task.task_details.mapper

import com.yotfr.sunmoon.domain.model.task.SubTask
import com.yotfr.sunmoon.presentation.task.task_details.model.SubTaskModel

class SubTaskMapper {

    fun fromDomain(domainModel: SubTask): SubTaskModel {
        return SubTaskModel(
            subTaskId = domainModel.subTaskId,
            subTaskDescription = domainModel.subTaskDescription,
            completionStatus = domainModel.completionStatus,
            taskId = domainModel.taskId
        )
    }

    fun toDomain(uiModel: SubTaskModel): SubTask {
        return SubTask(
            subTaskId = uiModel.subTaskId,
            subTaskDescription = uiModel.subTaskDescription,
            completionStatus = uiModel.completionStatus ?: false,
            taskId = uiModel.taskId
        )
    }

    fun fromDomainList(initial: List<SubTask>): List<SubTaskModel> {
        return initial.map { fromDomain(it) }
    }

    fun toDomainList(initial: List<SubTaskModel>): List<SubTask> {
        return initial.map { toDomain(it) }
    }

}