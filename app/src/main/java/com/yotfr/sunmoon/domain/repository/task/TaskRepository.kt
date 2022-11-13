package com.yotfr.sunmoon.domain.repository.task

import com.yotfr.sunmoon.data.data_source.model.relations.TaskWithSubTasksRelation
import com.yotfr.sunmoon.data.data_source.model.task.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(taskEntity: TaskEntity):Long

    suspend fun getTaskById(taskId:Long): Flow<TaskWithSubTasksRelation?>

    suspend fun deleteTask(taskEntity: TaskEntity)

    fun getScheduledUserTasks(
        selectedDate:Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    fun getScheduledCompletedUserTasks(
        selectedDate:Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    fun getUnplannedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    fun getUnplannedCompletedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    fun getTrashedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    fun getTrashedCompletedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    fun getOutdatedUserTasks(
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    fun getOutdatedCompletedUserTasks(
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    suspend fun deleteAllTrashedTasks()

    suspend fun deleteAllCompletedTrashedTasks()

    suspend fun deleteAllCompletedTasks()

    suspend fun deleteAllScheduledTasks()

    suspend fun deleteAllScheduledCompletedTasks()

    suspend fun deleteScheduledTasksOfSelectedDay(scheduledDate:Long)

    suspend fun deleteScheduledCompletedTasksOfSelectedDay(scheduledDate:Long)

    suspend fun deleteAllOutdatedTasks(expDate: Long)

    suspend fun deleteAllOutdatedCompletedTasks(expDate: Long)

    suspend fun deleteAllUnplannedTasks()

    suspend fun deleteAllUnplannedCompletedTasks()

    suspend fun getAllremindedTasks():List<TaskEntity>
}
