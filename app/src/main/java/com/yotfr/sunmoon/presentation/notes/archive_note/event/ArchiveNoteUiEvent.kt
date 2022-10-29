package com.yotfr.sunmoon.presentation.notes.archive_note.event

import com.yotfr.sunmoon.presentation.notes.archive_note.model.ArchiveNoteModel

sealed interface ArchiveNoteUiEvent {

    data class ShowUndoDeleteSnackbar(val note: ArchiveNoteModel): ArchiveNoteUiEvent

    object ShowUnarchiveSnackbar: ArchiveNoteUiEvent

}