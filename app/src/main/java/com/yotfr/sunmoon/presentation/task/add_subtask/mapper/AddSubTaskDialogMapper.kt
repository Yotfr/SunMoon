package com.yotfr.sunmoon.presentation.task.add_subtask.mapper

import com.yotfr.sunmoon.domain.model.task.SubTask
import com.yotfr.sunmoon.presentation.task.add_subtask.model.AddSubTaskDialogModel

class AddSubTaskDialogMapper {

    fun fromDomain(domainModel: SubTask): AddSubTaskDialogModel {
        return AddSubTaskDialogModel(
            subTaskId = domainModel.subTaskId,
            subTaskDescription = domainModel.subTaskDescription,
            completionStatus = domainModel.completionStatus,
            taskId = domainModel.taskId
        )
    }

    fun toDomain(uiModel: AddSubTaskDialogModel): SubTask {
        return SubTask(
            subTaskId = uiModel.subTaskId,
            subTaskDescription = uiModel.subTaskDescription,
            completionStatus = uiModel.completionStatus,
            taskId = uiModel.taskId
        )
    }

}