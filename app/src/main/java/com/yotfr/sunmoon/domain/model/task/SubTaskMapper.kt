package com.yotfr.sunmoon.domain.model.task

import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity

class SubTaskMapper{

    fun mapFromEntity(entity: SubTaskEntity): SubTask {
        return SubTask(
            subTaskId = entity.subTaskId,
            subTaskDescription = entity.subTaskDescription,
            completionStatus = entity.completionStatus,
            taskId = entity.taskId
        )
    }

    fun mapToEntity(domainModel: SubTask): SubTaskEntity {
        return SubTaskEntity(
            subTaskId = domainModel.subTaskId,
            subTaskDescription = domainModel.subTaskDescription,
            completionStatus = domainModel.completionStatus,
            taskId = domainModel.taskId
        )
    }

     fun mapFromEntityList(initial: List<SubTaskEntity>): List<SubTask> {
        return initial.map { mapFromEntity(it) }
    }

}