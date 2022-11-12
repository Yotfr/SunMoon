package com.yotfr.sunmoon.presentation.task.add_task.event

sealed interface BottomSheetAddTaskUiEvent {

    data class NavigateToDateSelector(val date: Long?, val time: Long?) : BottomSheetAddTaskUiEvent

    data class PopBackStack(val date:Long?) : BottomSheetAddTaskUiEvent

}