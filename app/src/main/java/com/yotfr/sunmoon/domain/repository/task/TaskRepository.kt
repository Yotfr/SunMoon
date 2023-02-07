package com.yotfr.sunmoon.domain.repository.task

import com.yotfr.sunmoon.data.datasource.entity.relations.TaskWithSubTasksRelation
import com.yotfr.sunmoon.data.datasource.entity.task.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(taskEntity: TaskEntity): Long

    suspend fun getTaskById(taskId: Long): Flow<TaskWithSubTasksRelation?>

    suspend fun deleteTask(taskEntity: TaskEntity)

    fun getScheduledTasks(
        selectedDate: Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    fun getScheduledCompletedTasks(
        selectedDate: Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    fun getUnplannedTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    fun getUnplannedCompletedTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    fun getTrashedTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    fun getTrashedCompletedTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    fun getOutdatedTasks(
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    fun getOutdatedCompletedTasks(
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    suspend fun deleteAllTrashedTasks()

    suspend fun deleteAllCompletedTrashedTasks()

    suspend fun deleteAllCompletedTasks()

    suspend fun deleteAllScheduledTasks()

    suspend fun deleteAllScheduledCompletedTasks()

    suspend fun deleteScheduledTasksOfSelectedDay(scheduledDate: Long)

    suspend fun deleteScheduledCompletedTasksOfSelectedDay(scheduledDate: Long)

    suspend fun deleteAllOutdatedTasks(expDate: Long)

    suspend fun deleteAllOutdatedCompletedTasks(expDate: Long)

    suspend fun deleteAllUnplannedTasks()

    suspend fun deleteAllUnplannedCompletedTasks()

    suspend fun getAllRemindedTasks(): List<TaskEntity>
}
