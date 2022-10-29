package com.yotfr.sunmoon.data.repository

import com.yotfr.sunmoon.data.data_source.TaskDao
import com.yotfr.sunmoon.data.data_source.model.relations.TaskWithSubTasksRelation
import com.yotfr.sunmoon.data.data_source.model.task.TaskEntity
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun insertTask(taskEntity: TaskEntity): Long {
        return taskDao.insertTask(taskEntity)
    }

    override suspend fun getTaskById(taskId: Long): Flow<TaskWithSubTasksRelation?> {
        return taskDao.getTaskById(taskId)
    }

    override suspend fun deleteTask(taskEntity: TaskEntity) {
        taskDao.deleteTask(taskEntity)
    }

    override fun getScheduledUserTasks(
        selectedDate: Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getUserScheduledTasks(selectedDate, searchQuery, expDate)
    }

    override fun getScheduledCompletedUserTasks(
        selectedDate: Long,
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getUserScheduledCompletedTasks(selectedDate, searchQuery, expDate)
    }

    override fun getUnplannedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getUserUnplannedTasks(searchQuery)
    }

    override fun getUnplannedCompletedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getUserUnplannedCompletedTasks(searchQuery)
    }

    override fun getTrashedUserTasks(
        searchQuery: String
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getTrashedUserTasks(searchQuery)
    }

    override fun getTrashedCompletedUserTasks(searchQuery: String): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getTrashedCompletedUserTasks(searchQuery)
    }

    override fun getOutdatedUserTasks(
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getOutdatedUserTasks(expDate, searchQuery)
    }

    override fun getOutdatedCompletedUserTasks(
        searchQuery: String,
        expDate: Long
    ): Flow<List<TaskWithSubTasksRelation>> {
        return taskDao.getOutdatedCompletedUserTasks(expDate, searchQuery)
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