package com.yotfr.sunmoon.data

import android.content.Context
import android.content.SharedPreferences
import com.yotfr.sunmoon.domain.repository.sharedpreference.PreferencesHelper

class AppPreferencesHelper(
    context: Context
): PreferencesHelper {

    private val sharedPreferences:SharedPreferences = context.getSharedPreferences(
        APP_PREFERENCES, Context.MODE_PRIVATE
    )

    companion object {
        private const val APP_PREFERENCES = "APP_PREFERENCES"
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val USER_ID = "USER_ID"
        private const val THEME = "THEME"
        private const val DATE_FORMAT = "DATE_FORMAT"
        private const val TIME_FORMAT = "TIME_FORMAT"
    }

    override fun updateAuthToken(token: String) {
        sharedPreferences.edit()
            .putString(AUTH_TOKEN,token)
            .apply()
    }

    override fun getAccessToken(): String? {
        return sharedPreferences.getString(
            AUTH_TOKEN,
            null
        )
    }

    override fun updateUserId(userId: String) {
        sharedPreferences.edit()
            .putString(USER_ID,userId)
            .apply()
    }

    override fun getUserId(): String? {
        return sharedPreferences.getString(
            USER_ID,
            null
        )
    }

    override fun updateTheme(theme: String) {
       sharedPreferences.edit()
           .putString(THEME,theme)
           .apply ()
    }

    override fun getTheme(): String? {
        return sharedPreferences.getString(
            THEME,
            null
        )
    }

    override fun updateDateFormat(dateFormat: String) {
        sharedPreferences.edit()
            .putString(DATE_FORMAT, dateFormat)
            .apply ()
    }

    override fun getDateFormat(): String? {
        return sharedPreferences.getString(
            DATE_FORMAT,
            "dd/MM/yyyy"
        )
    }

    override fun updateTimeFormat(timeFormat: String) {
        sharedPreferences.edit()
            .putString(TIME_FORMAT, timeFormat)
            .apply ()
    }

    override fun getTimeFormat(): String {
        return sharedPreferences.getString(
            TIME_FORMAT,
            "HH:mm"
        ) ?: "HH:mm"
    }
}