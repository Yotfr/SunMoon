package com.yotfr.sunmoon.data.repository

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

//TODO:create constants file
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DATA_STORE")

class DataStoreRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DataStoreRepository {

    override suspend fun updateTheme(theme: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.THEME] = theme
        }
    }

    override suspend fun getTheme(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[PreferencesKeys.THEME]
    }

    override suspend fun updateDateFormat(dateFormat: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.DATE_FORMAT] = dateFormat
        }
    }

    override suspend fun getDateFormat(): Flow<String?> {
        val dateFormat = context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("PREFMANAGER", "Error reading data store", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.DATE_FORMAT]
            }
        return dateFormat
    }

    override suspend fun getTimePattern(): Flow<String> {
        val datePattern = context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("PREFMANAGER", "Error reading data store", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.TIME_PATTERN]
                    ?: if (DateFormat.is24HourFormat(context)) {
                        "HH:mm"
                    } else "h:mm a"
            }
        return datePattern
    }

    override suspend fun getTimeFormat(): Flow<Int?> {
        val timeFormat = context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("PREFMANAGER", "Error reading data store", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.TIME_FORMAT]
            }
        return timeFormat
    }

    override suspend fun updateTimeFormat(timePattern: String, timeFormat: Int) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.TIME_PATTERN] = timePattern
            settings[PreferencesKeys.TIME_FORMAT] = timeFormat
        }
    }

    override suspend fun getDateTimeSettings(): Flow<Triple<String?, String, Int?>> {
        val settings = context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e("PREFMANAGER", "Error reading data store", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                Triple(
                    preferences[PreferencesKeys.DATE_FORMAT],
                    preferences[PreferencesKeys.TIME_PATTERN] ?: if (DateFormat.is24HourFormat(
                            context
                        )
                    ) {
                        "HH:mm"
                    } else "h:mm a",
                    preferences[PreferencesKeys.TIME_FORMAT]
                )
            }
        return settings
    }

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("THEME")
        val DATE_FORMAT = stringPreferencesKey("DATE_FORMAT")
        val TIME_PATTERN = stringPreferencesKey("TIME_PATTERN")
        val TIME_FORMAT = intPreferencesKey("TIME_FORMAT")
    }
}