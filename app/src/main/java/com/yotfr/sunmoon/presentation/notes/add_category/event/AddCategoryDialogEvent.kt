package com.yotfr.sunmoon.presentation.notes.add_category.event

sealed interface AddCategoryDialogEvent {
    data class AddCategory(val categoryText: String) : AddCategoryDialogEvent
}
