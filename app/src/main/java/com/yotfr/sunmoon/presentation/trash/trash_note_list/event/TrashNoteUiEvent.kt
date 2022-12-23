package com.yotfr.sunmoon.presentation.trash.trash_note_list.event

import com.yotfr.sunmoon.presentation.trash.trash_note_list.model.TrashNoteModel

sealed interface TrashNoteUiEvent {
    data class ShowUndoDeleteSnackbar(val note: TrashNoteModel) : TrashNoteUiEvent
    object ShowRestoreSnackbar : TrashNoteUiEvent
}
