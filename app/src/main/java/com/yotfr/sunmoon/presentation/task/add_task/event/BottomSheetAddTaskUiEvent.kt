package com.yotfr.sunmoon.presentation.task.add_task.event

sealed interface BottomSheetAddTaskUiEvent {

    data class NavigateToDateSelector(val date: Long?, val time: Long?) : BottomSheetAddTaskUiEvent

    object PopBackStack : BottomSheetAddTaskUiEvent

}