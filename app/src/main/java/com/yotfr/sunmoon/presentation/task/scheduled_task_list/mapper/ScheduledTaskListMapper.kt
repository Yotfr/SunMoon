package com.yotfr.sunmoon.presentation.task.scheduled_task_list.mapper

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class ScheduledTaskListMapper {

    fun fromDomain(domainModel: Task, sdfPattern: String): ScheduledTaskListModel {
        return ScheduledTaskListModel(
            taskId = domainModel.taskId ?: throw IllegalArgumentException("Not found taskId"),
            taskDescription = domainModel.taskDescription,
            isCompleted = domainModel.isCompleted,
            isTrashed = domainModel.isTrashed,
            scheduledDate = domainModel.scheduledDate,
            scheduledTime = domainModel.scheduledTime,
            scheduledFormattedTime = formatTime(domainModel.scheduledTime, sdfPattern),
            isAddTimeButtonVisible = domainModel.scheduledTime == null,
            completionProgress = calculateProgress(domainModel),
            remindDate = domainModel.remindDate,
            remindTime = domainModel.remindTime,
            remindDelayTime = domainModel.remindDelayTime,
            importance = domainModel.importance
        )
    }

    fun toDomain(uiModel: ScheduledTaskListModel): Task {
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

    fun fromDomainList(initial: List<Task>, sdfPattern: String): List<ScheduledTaskListModel> {
        return initial.map { fromDomain(it, sdfPattern) }
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

    private fun formatTime(time: Long?, sdfPattern: String): String {
        val sdf = SimpleDateFormat(sdfPattern, Locale.getDefault())
        if (time == null) return ""
        return sdf.format(time)
    }
}
