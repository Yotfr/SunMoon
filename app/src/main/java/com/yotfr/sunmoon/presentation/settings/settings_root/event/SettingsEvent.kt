package com.yotfr.sunmoon.presentation.settings.settings_root.event

sealed interface SettingsEvent {
    data class ChangeDateFormat(val dateFormat: String) : SettingsEvent
    data class ChangeTimeFormat(val timePattern: String, val timeFormat: Int) : SettingsEvent
}
