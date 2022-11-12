package com.yotfr.sunmoon.presentation.settings.settings_theme.event

interface SettingsThemeEvent {

    data class ChangeTheme(val newTheme:String):SettingsThemeEvent

    object SaveTheme:SettingsThemeEvent
}