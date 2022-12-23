package com.yotfr.sunmoon.presentation.settings.settings_root.model

data class SettingsUiStateModel(
    val datePattern: DatePattern = DatePattern.DAY_FIRST,
    val timeFormat: TimeFormat = TimeFormat.SYSTEM_DEFAULT
)
