package com.yotfr.sunmoon.presentation.notes.note_list.event
import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListModel

sealed interface NoteListUiEvent {

    data class ShowUndoDeleteSnackbar(val note: NoteListModel) : NoteListUiEvent

    data class ShowUndoArchiveSnackbar(val note: NoteListModel) : NoteListUiEvent
}
