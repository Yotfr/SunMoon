package com.yotfr.sunmoon.presentation.task.task_details.mapper

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.presentation.task.task_details.model.TaskDetailsModel

class TaskDetailsMapper {

    private val subTaskMapper = SubTaskMapper()

    fun fromDomain(domainModel: Task): TaskDetailsModel {
        return TaskDetailsModel(
            taskId = domainModel.taskId,
            taskDescription = domainModel.taskDescription,
            completionStatus = domainModel.isCompleted,
            isTrashed = domainModel.isTrashed,
            scheduledDate = domainModel.scheduledDate,
            scheduledTime = domainModel.scheduledTime,
            subTasks = subTaskMapper.fromDomainList(domainModel.subTasks, !domainModel.isCompleted),
            completionProgress = calculateProgress(domainModel),
            remindDate = domainModel.remindDate,
            remindTime = domainModel.remindTime,
            remindDelayTime = domainModel.remindDelayTime,
            importance = domainModel.importance
        )
    }

    fun toDomain(uiModel: TaskDetailsModel): Task {
        return Task(
            taskId = uiModel.taskId,
            taskDescription = uiModel.taskDescription,
            isCompleted = uiModel.completionStatus,
            isTrashed = uiModel.isTrashed,
            scheduledDate = uiModel.scheduledDate,
            scheduledTime = uiModel.scheduledTime,
            subTasks = subTaskMapper.toDomainList(uiModel.subTasks),
            remindDate = uiModel.remindDate,
            remindTime = uiModel.remindTime,
            remindDelayTime = uiModel.remindDelayTime,
            importance = uiModel.importance
        )
    }

    private fun calculateProgress(domainModel: Task): Int {
        if (domainModel.isCompleted) return 100
        return (
            (
                domainModel.subTasks.count { it.completionStatus }.toDouble() /
                    domainModel.subTasks.size
                ) * 100
            ).toInt()
    }
}
