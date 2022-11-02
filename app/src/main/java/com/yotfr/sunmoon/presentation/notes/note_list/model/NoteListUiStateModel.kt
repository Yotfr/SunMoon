package com.yotfr.sunmoon.presentation.notes.note_list.model

data class NoteListUiStateModel(
    val notes:List<NoteListModel>,
    val footerState:NoteListFooterModel
)