package com.yotfr.sunmoon.presentation.notes.note_list.model

data class CategoryNoteListModel(
    val id: Long,
    val categoryDescription: String,
    val isVisible: Boolean,
    val notes: List<NoteListModel>
)
