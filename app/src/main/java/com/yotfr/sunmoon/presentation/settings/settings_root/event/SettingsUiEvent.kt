package com.yotfr.sunmoon.presentation.settings.settings_root.event

sealed interface SettingsUiEvent {
    object RestartActivity: SettingsUiEvent
}