package com.yotfr.sunmoon.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.yotfr.sunmoon.data.datasource.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val SETTINGS_PREFERENCES = "SETTINGS_PREFERENCES"

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
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(SETTINGS_PREFERENCES) }
        )
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
