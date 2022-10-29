package com.yotfr.sunmoon.domain.repository.sharedpreference

interface PreferencesHelper {
    fun updateAuthToken(token:String)
    fun getAccessToken():String?
    fun updateUserId(userId:String)
    fun getUserId():String?
    fun updateTheme(theme:String)
    fun getTheme():String?
    fun updateDateFormat(dateFormat:String)
    fun getDateFormat():String?
    fun updateTimeFormat(timeFormat:String)
    fun getTimeFormat():String
}