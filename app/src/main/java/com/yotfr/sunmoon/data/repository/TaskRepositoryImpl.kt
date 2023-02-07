package com.yotfr.sunmoon.data.repository

import com.yotfr.sunmoon.data.datasource.TaskDao
import com.yotfr.sunmoon.data.datasource.entity.relations.TaskWithSubTasksRelation
import com.yotfr.sunmoon.data.datasource.entity.task.TaskEntity
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun upsertTask(taskEntity: TaskEntity): Long {
        return taskDao.insertTask(taskEntity)
    }

    override suspend fun getTaskById(taskId: Long): Flow<TaskWithSubTasksRelation?> {
        return taskDao.getTaskById(taskId)
    }

    override suspend fun getAllRemindedTasks(): List<TaskEntity> {
        return taskDao.getAllRemindedTasks()
    }

    override suspend fun deleteTask(taskEntity: TaskEntity) {
        taskDao.deleteTask(taskEntity)
    }

    override fun getScheduledTasks(
        selectedDate: Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getScheduledTasks(selectedDate, searchQuery, expDate)
    }

    override fun getScheduledCompletedTasks(
        selectedDate: Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getScheduledCompletedTasks(selectedDate, searchQuery, expDate)
    }

    override fun getUnplannedTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getUnplannedTasks(searchQuery)
    }

    override fun getUnplannedCompletedTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getUnplannedCompletedTasks(searchQuery)
    }

    override fun getTrashedTasks(
        searchQuery: String
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getTrashedTasks(searchQuery)
    }

    override fun getTrashedCompletedTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getTrashedCompletedTasks(searchQuery)
    }

    override fun getOutdatedTasks(
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getOutdatedTasks(expDate, searchQuery)
    }

    override fun getOutdatedCompletedTasks(
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getOutdatedCompletedTasks(expDate, searchQuery)
    }

    override suspend fun deleteAllTrashedTasks() {
        return taskDao.deleteAllTrashed()
    }

    override suspend fun deleteAllCompletedTrashedTasks() {
        return taskDao.deleteAllCompletedTrashed()
    }

    override suspend fun deleteAllCompletedTasks() {
        return taskDao.deleteAllCompletedTasks()
    }

    override suspend fun deleteAllScheduledTasks() {
        return taskDao.deleteAllScheduledTasks()
    }

    override suspend fun deleteScheduledTasksOfSelectedDay(scheduledDate: Long) {
        return taskDao.deleteScheduledTasksOfSelectedDay(scheduledDate)
    }

    override suspend fun deleteAllOutdatedTasks(expDate: Long) {
        return taskDao.deleteAllOutdatedTasks(expDate)
    }

    override suspend fun deleteAllUnplannedTasks() {
        return taskDao.deleteAllUnplannedTasks()
    }

    override suspend fun deleteAllScheduledCompletedTasks() {
        taskDao.deleteAllScheduledCompletedTasks()
    }

    override suspend fun deleteScheduledCompletedTasksOfSelectedDay(scheduledDate: Long) {
        taskDao.deleteScheduledCompletedTasksOfSelectedDay(scheduledDate)
    }

    override suspend fun deleteAllOutdatedCompletedTasks(expDate: Long) {
        taskDao.deleteAllOutdatedCompletedTasks(expDate)
    }

    override suspend fun deleteAllUnplannedCompletedTasks() {
        taskDao.deleteAllUnplannedCompletedTasks()
    }
}
