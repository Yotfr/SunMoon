package com.yotfr.sunmoon.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yotfr.sunmoon.data.datasource.entity.note.CategoryEntity
import com.yotfr.sunmoon.data.datasource.entity.note.NoteEntity
import com.yotfr.sunmoon.data.datasource.entity.task.SubTaskEntity
import com.yotfr.sunmoon.data.datasource.entity.task.TaskEntity

@Database(
    version = 1,
    entities = [
        TaskEntity::class,
        NoteEntity::class,
        SubTaskEntity::class,
        CategoryEntity::class
    ],
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val noteDao: NoteDao
    abstract val subTaskDao: SubTaskDao
    abstract val categoryDao: CategoryDao
}
