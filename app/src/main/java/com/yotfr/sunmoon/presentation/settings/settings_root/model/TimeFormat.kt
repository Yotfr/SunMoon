package com.yotfr.sunmoon.presentation.settings.settings_root.model

enum class TimeFormat(val format: Int) {
    AM_PM(format = com.google.android.material.timepicker.TimeFormat.CLOCK_12H),
    NORMAL(format = com.google.android.material.timepicker.TimeFormat.CLOCK_24H),
    SYSTEM_DEFAULT(format = 2)
}