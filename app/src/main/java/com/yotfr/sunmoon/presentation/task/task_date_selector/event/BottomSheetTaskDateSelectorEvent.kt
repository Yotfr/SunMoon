package com.yotfr.sunmoon.presentation.task.task_date_selector.event

sealed interface BottomSheetTaskDateSelectorEvent {

    data class DateTimeChanged(val date: Long? = null, val time: Long? = null) :
        BottomSheetTaskDateSelectorEvent

    object SaveDateTimePressed : BottomSheetTaskDateSelectorEvent

    object ClearDateTime : BottomSheetTaskDateSelectorEvent
}