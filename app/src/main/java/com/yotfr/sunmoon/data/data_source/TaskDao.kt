package com.yotfr.sunmoon.data.data_source

import androidx.room.*
import com.yotfr.sunmoon.data.data_source.model.relations.TaskWithSubTasksRelation
import com.yotfr.sunmoon.data.data_source.model.task.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(entity = TaskEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(taskEntity: TaskEntity): Long

    @Delete(entity = TaskEntity::class)
    suspend fun deleteTask(taskEntity: TaskEntity)

    @Transaction
    @Query(value = "SELECT * FROM task WHERE taskId = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskWithSubTasksRelation?>

    @Transaction
    @Query(value = "SELECT * FROM task WHERE isCompleted = 0 AND isTrashed = 0  AND scheduledDate = :selectedDate AND taskDescription LIKE '%' || :searchQuery || '%' AND scheduledDate >= :expDate ORDER BY importance DESC, scheduledTime")
    fun getUserScheduledTasks(
        selectedDate: Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    @Transaction
    @Query(value = "SELECT * FROM task WHERE isCompleted = 1 AND isTrashed = 0  AND scheduledDate = :selectedDate AND taskDescription LIKE '%' || :searchQuery || '%' AND scheduledDate >= :expDate ORDER BY scheduledTime")
    fun getUserScheduledCompletedTasks(
        selectedDate: Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>>

    @Transaction
    @Query(value = "SELECT * FROM task WHERE isCompleted = 0 AND isTrashed = 0  AND scheduledDate is null AND scheduledTime is null AND taskDescription LIKE '%' || :searchQuery || '%' ORDER BY importance DESC")
    fun getUserUnplannedTasks(
        searchQuery: String
    ): Flow<List<TaskWithSubTasksRelation>>

    @Transaction
    @Query(value = "SELECT * FROM task WHERE isCompleted = 1 AND isTrashed = 0  AND scheduledDate is null AND scheduledTime is null AND taskDescription LIKE '%' || :searchQuery || '%'")
    fun getUserUnplannedCompletedTasks(
        searchQuery: String
    ): Flow<List<TaskWithSubTasksRelation>>

    @Transaction
    @Query(value = "SELECT * FROM task WHERE isTrashed = 1 AND isCompleted = 0 AND taskDescription LIKE '%' || :searchQuery || '%'")
    fun getTrashedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    @Transaction
    @Query(value = "SELECT * FROM task WHERE isTrashed = 1 AND isCompleted = 1 AND taskDescription LIKE '%' || :searchQuery || '%'")
    fun getTrashedCompletedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    @Transaction
    @Query(value = "SELECT * FROM task WHERE isCompleted = 0 AND isTrashed = 0 AND scheduledDate < :expDate AND taskDescription LIKE '%' || :searchQuery || '%'")
    fun getOutdatedUserTasks(expDate: Long, searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    @Transaction
    @Query(value = "SELECT * FROM task WHERE isCompleted = 1 AND isTrashed = 0 AND scheduledDate < :expDate AND taskDescription LIKE '%' || :searchQuery || '%'")
    fun getOutdatedCompletedUserTasks(expDate: Long, searchQuery: String): Flow<List<TaskWithSubTasksRelation>>

    @Query(value = "DELETE FROM task WHERE isTrashed = 1")
    suspend fun deleteAllTrashed()

    @Query(value = "DELETE FROM task WHERE isTrashed = 1 AND isCompleted = 1")
    suspend fun deleteAllCompletedTrashed()

    @Query(value = "DELETE FROM task WHERE isCompleted = 1")
    suspend fun deleteAllCompletedTasks()

    @Query("DELETE FROM task WHERE scheduledDate < :expDate")
    suspend fun deleteAllOutdatedTasks(expDate: Long)

    @Query(value = "DELETE FROM task WHERE scheduledDate < :expDate AND isCompleted = 1")
    suspend fun deleteAllOutdatedCompletedTasks(expDate: Long)

    @Query(value = "DELETE FROM task WHERE isTrashed = 0 AND scheduledDate is null AND scheduledTime is null")
    suspend fun deleteAllUnplannedTasks()

    @Query(value = "DELETE FROM task WHERE isTrashed = 0 AND scheduledDate is null AND scheduledTime is null AND isCompleted = 1")
    suspend fun deleteAllUnplannedCompletedTasks()

    @Query(value = "DELETE FROM task WHERE isTrashed = 0 AND scheduledDate is not null")
    suspend fun deleteAllScheduledTasks()

    @Query(value = "DELETE FROM task WHERE isTrashed = 0 AND scheduledDate is not null AND isCompleted = 1")
    suspend fun deleteAllScheduledCompletedTasks()

    @Query(value = "DELETE FROM task WHERE isTrashed = 0 AND scheduledDate = :scheduledDate")
    suspend fun deleteScheduledTasksOfSelectedDay(scheduledDate: Long)

    @Query(value = "DELETE FROM task WHERE isTrashed = 0 AND scheduledDate = :scheduledDate AND isCompleted = 1")
    suspend fun deleteScheduledCompletedTasksOfSelectedDay(scheduledDate: Long)

    @Query(value = "SELECT * FROM task WHERE remindDate != null")
    suspend fun getAllRemindedTasks(): List<TaskEntity>
}
