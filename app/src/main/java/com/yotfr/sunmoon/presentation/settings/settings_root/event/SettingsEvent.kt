package com.yotfr.sunmoon.presentation.settings.settings_root.event

import com.yotfr.sunmoon.presentation.settings.settings_root.model.LanguageCode

sealed interface SettingsEvent {
    data class ChangeDateFormat(val dateFormat: String) : SettingsEvent
    data class ChangeTimeFormat(val timePattern: String, val timeFormat: Int) : SettingsEvent
    data class ChangeLanguage(val language: LanguageCode) : SettingsEvent
}
