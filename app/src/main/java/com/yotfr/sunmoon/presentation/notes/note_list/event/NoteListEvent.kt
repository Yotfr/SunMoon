package com.yotfr.sunmoon.presentation.notes.note_list.event

import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListModel

sealed interface NoteListEvent {

    data class ArchiveNote(val note: NoteListModel) : NoteListEvent

    data class UndoArchiveNote(val note: NoteListModel) : NoteListEvent

    data class DeleteNote(val note: NoteListModel) : NoteListEvent

    data class UndoDeleteNote(val note: NoteListModel) : NoteListEvent

    data class ChangeSelectedCategory(val selectedCategoryId: Long) : NoteListEvent

    data class UpdateSearchQuery(val searchQuery: String) : NoteListEvent

    data class PinUnpinNote(val note: NoteListModel) : NoteListEvent

    object DeleteAllUnarchivedNote : NoteListEvent
}
