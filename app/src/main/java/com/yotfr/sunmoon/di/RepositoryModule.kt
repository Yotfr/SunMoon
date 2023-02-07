package com.yotfr.sunmoon.di

import com.yotfr.sunmoon.data.repository.*
import com.yotfr.sunmoon.domain.repository.datastore.DataStoreRepository
import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCategoryRepository(
        categoryRepository: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    abstract fun bindNoteRepository(
        noteRepository: NoteRepositoryImpl
    ): NoteRepository

    @Binds
    abstract fun bindSubTaskRepository(
        subTaskRepository: SubTaskRepositoryImpl
    ): SubTaskRepository

    @Binds
    abstract fun bindTaskRepository(
        taskRepository: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    abstract fun bindDataStoreRepository(
        dataStoreRepository: DataStoreRepositoryImpl
    ): DataStoreRepository
}
