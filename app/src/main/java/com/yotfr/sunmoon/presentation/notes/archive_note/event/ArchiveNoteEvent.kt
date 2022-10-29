package com.yotfr.sunmoon.presentation.notes.archive_note.event

import com.yotfr.sunmoon.presentation.notes.archive_note.model.ArchiveNoteModel

sealed interface ArchiveNoteEvent {

    data class DeleteArchiveNote(val note: ArchiveNoteModel): ArchiveNoteEvent

    data class UnarchiveArchiveNote(val note: ArchiveNoteModel): ArchiveNoteEvent

    data class UndoDeleteArchiveNote(val note: ArchiveNoteModel): ArchiveNoteEvent

    data class UpdateSearchQuery(val searchQuery: String): ArchiveNoteEvent

    object DeleteAllUnarchivedNote: ArchiveNoteEvent

}