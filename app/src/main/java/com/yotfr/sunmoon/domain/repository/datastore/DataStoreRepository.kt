package com.yotfr.sunmoon.domain.repository.datastore

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun updateTheme(theme: String)
    suspend fun getTheme(): String?
    suspend fun updateDateFormat(dateFormat: String)
    suspend fun getDateFormat(): Flow<String>
    suspend fun updateTimeFormat(timePattern: String, timeFormat: Int)
    suspend fun getTimePattern(): Flow<String>
    suspend fun getTimeFormat(): Flow<Int?>
    suspend fun getDateTimeSettings(): Flow<Triple<String?, String, Int?>>
}
