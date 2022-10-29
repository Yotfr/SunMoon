package com.yotfr.sunmoon.presentation.notes.add_edit_note.event

import com.yotfr.sunmoon.presentation.notes.add_edit_note.model.AddEditNoteCategoryModel


sealed interface AddEditNoteEvent {

    data class SaveNotePressed(
        val title:String,
        val text:String
        ): AddEditNoteEvent

    data class ChangeSelectedCategory(
        val newSelectedCategory:AddEditNoteCategoryModel
    ): AddEditNoteEvent

    object ClearSelectedCategory: AddEditNoteEvent

}