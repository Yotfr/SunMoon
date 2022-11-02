package com.yotfr.sunmoon.presentation.notes.archive_note.model

data class ArchiveNoteUiState(
    val notes: List<ArchiveNoteModel>,
    val footerState: ArchiveNoteFooterModel
)