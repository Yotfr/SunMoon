package com.yotfr.sunmoon.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yotfr.sunmoon.data.data_source.model.note.CategoryEntity
import com.yotfr.sunmoon.data.data_source.model.note.NoteEntity
import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity
import com.yotfr.sunmoon.data.data_source.model.task.TaskEntity

@Database(
    version = 1,
    entities = [
        TaskEntity::class,
        NoteEntity::class,
        SubTaskEntity::class,
        CategoryEntity::class
    ]
)
abstract class AppDataBase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val noteDao: NoteDao
    abstract val subTaskDao: SubTaskDao
    abstract val categoryDao: CategoryDao
}