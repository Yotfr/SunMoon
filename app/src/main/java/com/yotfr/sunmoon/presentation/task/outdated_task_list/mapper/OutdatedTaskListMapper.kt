package com.yotfr.sunmoon.presentation.task.outdated_task_list.mapper

import com.yotfr.sunmoon.domain.model.task.Task
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListModel
import java.text.SimpleDateFormat
import java.util.*

class OutdatedTaskListMapper {
    fun fromDomain(
        domainModel: Task,
        sdfPattern: String,
        currentTime: Long
    ): OutdatedTaskListModel {

        return OutdatedTaskListModel(
            taskId = domainModel.taskId ?: throw IllegalArgumentException("Not found taskId"),
            taskDescription = domainModel.taskDescription,
            isCompleted = domainModel.isCompleted,
            isTrashed = domainModel.isTrashed,
            scheduledDate = domainModel.scheduledDate,
            scheduledTime = domainModel.scheduledTime,
            scheduledFormattedTime = formatTime(domainModel.scheduledTime, sdfPattern),
            formattedOverDueTime = formatOverDueTime(
                domainModel.scheduledTime,
                currentTime,
                sdfPattern
            ),
            completionProgress = calculateProgress(domainModel),
            isAddTimeButtonVisible = domainModel.scheduledTime == null,
            remindDate = domainModel.remindDate,
            remindTime = domainModel.remindTime,
            remindDelayTime = domainModel.remindDelayTime,
            importance = domainModel.importance
        )
    }

    fun toDomain(uiModel: OutdatedTaskListModel): Task {
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

    fun fromDomainList(initial: List<Task>, sdfPattern: String, currentTime: Long): List<OutdatedTaskListModel> {
        return initial.map { fromDomain(it, sdfPattern, currentTime) }
    }

    private fun calculateProgress(domainModel: Task): Int {
        if (domainModel.isCompleted) return 100
        return ((domainModel.subTasks.count { it.completionStatus }.toDouble() /
                domainModel.subTasks.size) * 100).toInt()
    }

    private fun formatTime(time: Long?, sdfPattern: String): String {
        val sdf = SimpleDateFormat(sdfPattern, Locale.getDefault())
        if (time == null) return ""
        return sdf.format(time)
    }

    private fun formatOverDueTime(
        scheduledTime: Long?,
        currentTime: Long,
        sdfPattern: String
    ): String {
        val sdf = SimpleDateFormat(sdfPattern, Locale.getDefault())
        if (scheduledTime == null) return ""
        val overDueTime = currentTime - scheduledTime
        return sdf.format(overDueTime)
    }

}