package com.yotfr.sunmoon.presentation.task.unplanned_task_list.mapper

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListModel
import java.lang.IllegalArgumentException

class UnplannedTaskListMapper {
    fun fromDomain(domainModel: Task): UnplannedTaskListModel {

        return UnplannedTaskListModel(
            taskId = domainModel.taskId ?: throw IllegalArgumentException("Not found taskId"),
            taskDescription = domainModel.taskDescription,
            isCompleted = domainModel.isCompleted,
            isTrashed = domainModel.isTrashed,
            scheduledDate = domainModel.scheduledDate,
            scheduledTime = domainModel.scheduledTime,
            completionProgress = calculateProgress(domainModel),
            isAddTimeButtonVisible = domainModel.scheduledTime == null,
            remindDate = domainModel.remindDate,
            remindTime = domainModel.remindTime,
            remindDelayTime = domainModel.remindDelayTime,
            importance = domainModel.importance
        )
    }

    fun toDomain(uiModel: UnplannedTaskListModel): Task {
        return Task(
            taskId = uiModel.taskId,
            taskDescription = uiModel.taskDescription,
            isCompleted = uiModel.isCompleted,
            isTrashed = uiModel.isTrashed,
            scheduledDate = uiModel.scheduledDate,
            scheduledTime = uiModel.scheduledTime,
            subTasks = emptyList(),
            remindDate = uiModel.remindDate,
            remindTime = uiModel.remindTime,
            remindDelayTime = uiModel.remindDelayTime,
            importance = uiModel.importance
        )
    }

    fun fromDomainList(initial:List<Task>):List<UnplannedTaskListModel>{
        return initial.map { fromDomain(it) }
    }

    private fun calculateProgress(domainModel: Task):Int {
        if (domainModel.isCompleted) return 100
        return ((domainModel.subTasks.count { it.completionStatus }.toDouble() /
                domainModel.subTasks.size) * 100).toInt()
    }
}