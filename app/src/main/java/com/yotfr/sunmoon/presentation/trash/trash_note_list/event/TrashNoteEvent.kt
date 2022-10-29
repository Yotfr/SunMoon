package com.yotfr.sunmoon.presentation.trash.trash_note_list.event


import com.yotfr.sunmoon.presentation.trash.trash_note_list.model.TrashNoteModel

sealed interface TrashNoteEvent {

    data class RestoreTrashedNote(val note: TrashNoteModel): TrashNoteEvent

    data class UpdateSearchQuery(val searchQuery:String): TrashNoteEvent

    data class DeleteTrashedNote(val note: TrashNoteModel): TrashNoteEvent

    data class UndoDeleteTrashedNote(val note: TrashNoteModel): TrashNoteEvent

    object DeleteAllTrashedNotes: TrashNoteEvent

}