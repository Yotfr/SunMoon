package com.yotfr.sunmoon.domain.model.task

import com.yotfr.sunmoon.data.data_source.model.relations.TaskWithSubTasksRelation
import com.yotfr.sunmoon.data.data_source.model.task.TaskEntity

class TaskMapper {

    private val subTaskMapper = SubTaskMapper()

    fun mapFromEntity(entity: TaskWithSubTasksRelation): Task {
        return Task(
            taskId = entity.taskEntity.taskId,
            taskDescription = entity.taskEntity.taskDescription,
            isCompleted = entity.taskEntity.isCompleted,
            isTrashed = entity.taskEntity.isTrashed,
            scheduledDate = entity.taskEntity.scheduledDate,
            scheduledTime = entity.taskEntity.scheduledTime,
            remindDate = entity.taskEntity.remindDate,
            remindTime = entity.taskEntity.remindTime,
            subTasks = subTaskMapper.mapFromEntityList(
                entity.nestedToDo
            ),
            importance = entity.taskEntity.importance,
            remindDelayTime = entity.taskEntity.remindDelayTime
        )
    }

  fun mapToEntity(domainModel: Task): TaskEntity {
        return TaskEntity(
            taskId = domainModel.taskId,
            taskDescription = domainModel.taskDescription,
            isCompleted = domainModel.isCompleted,
            isTrashed = domainModel.isTrashed,
            scheduledDate = domainModel.scheduledDate,
            scheduledTime = domainModel.scheduledTime,
            remindDate = domainModel.remindDate,
            remindTime = domainModel.remindTime,
            importance = domainModel.importance,
            remindDelayTime = domainModel.remindDelayTime
        )
    }

   fun mapFromEntityList(initial: List<TaskWithSubTasksRelation>): List<Task> {
        return initial.map { mapFromEntity(it) }
    }
}