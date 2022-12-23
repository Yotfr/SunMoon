package com.yotfr.sunmoon.di

import android.content.Context
import androidx.room.Room
import com.yotfr.sunmoon.data.data_source.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatasourceModule {

    @Provides
    @Singleton
    fun provideAppDatabases(@ApplicationContext appContext: Context): AppDataBase {
        return Room.databaseBuilder(
            appContext,
            AppDataBase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    fun provideCategoryDao(appDataBase: AppDataBase): CategoryDao {
        return appDataBase.categoryDao
    }

    @Provides
    fun provideNoteDao(appDataBase: AppDataBase): NoteDao {
        return appDataBase.noteDao
    }

    @Provides
    fun provideSubTaskDao(appDataBase: AppDataBase): SubTaskDao {
        return appDataBase.subTaskDao
    }

    @Provides
    fun provideTaskDao(appDataBase: AppDataBase): TaskDao {
        return appDataBase.taskDao
    }
}
