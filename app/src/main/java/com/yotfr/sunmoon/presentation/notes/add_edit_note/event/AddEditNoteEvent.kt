package com.yotfr.sunmoon.presentation.notes.add_edit_note.event

import com.yotfr.sunmoon.presentation.notes.add_edit_note.model.AddEditNoteCategoryModel

sealed interface AddEditNoteEvent {

    data class SaveNotePressed(
        val title: String,
        val text: String
    ) : AddEditNoteEvent

    data class ChangeSelectedCategory(
        val newSelectedCategory: AddEditNoteCategoryModel
    ) : AddEditNoteEvent

    data class SaveNewNoteTitle(
        val newTitle: String
    ) : AddEditNoteEvent

    data class SaveNewNoteText(
        val newText: String
    ) : AddEditNoteEvent

    object ApplySavedTextFields : AddEditNoteEvent

    object ClearSelectedCategory : AddEditNoteEvent
}
