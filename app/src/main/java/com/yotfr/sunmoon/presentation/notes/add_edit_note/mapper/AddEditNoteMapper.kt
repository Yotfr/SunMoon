package com.yotfr.sunmoon.presentation.notes.add_edit_note.mapper

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.presentation.notes.add_edit_note.model.AddEditNoteModel

class AddEditNoteMapper {
    fun fromDomain(domainModel: Note): AddEditNoteModel {
        return AddEditNoteModel(
            id = domainModel.noteId,
            importance = domainModel.isPinned,
            title = domainModel.title,
            text = domainModel.text,
            trashed = domainModel.trashed,
            created = domainModel.created,
            archived = domainModel.archived,
            categoryId = domainModel.categoryId
        )
    }
    fun toDomain(uiModel: AddEditNoteModel): Note {
        return Note(
            noteId = uiModel.id,
            isPinned = uiModel.importance,
            title = uiModel.title,
            text = uiModel.text,
            trashed = uiModel.trashed,
            created = uiModel.created ?: System.currentTimeMillis(),
            archived = uiModel.archived,
            categoryId = uiModel.categoryId
        )
    }
}
