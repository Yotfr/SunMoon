package com.yotfr.sunmoon.presentation.settings.settings_root.model

enum class DatePattern(val pattern: String) {
    YEAR_FIRST(pattern = "yyyy/MM/dd"),
    MONTH_FIRST(pattern = "MM/dd/yyyy"),
    DAY_FIRST(pattern = "dd/MM/yyyy")
}