package com.yotfr.sunmoon.presentation.task.task_date_selector.event

sealed interface BottomSheetDateSelectorUiEvent {
    data class SaveDateTime(val date: Long?, val time: Long?) : BottomSheetDateSelectorUiEvent
}
