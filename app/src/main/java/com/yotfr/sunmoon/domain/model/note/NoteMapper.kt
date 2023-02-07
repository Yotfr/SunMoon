package com.yotfr.sunmoon.domain.model.note

import com.yotfr.sunmoon.data.datasource.entity.note.NoteEntity

class NoteMapper {

    fun mapFromEntity(entity: NoteEntity): Note {
        return Note(
            noteId = entity.noteId,
            isPinned = entity.isPinned,
            title = entity.title,
            text = entity.text,
            trashed = entity.trashed,
            created = System.currentTimeMillis(),
            archived = entity.archived,
            categoryId = entity.categoryId
        )
    }

    fun mapToEntity(domainModel: Note): NoteEntity {
        return NoteEntity(
            noteId = domainModel.noteId,
            isPinned = domainModel.isPinned,
            title = domainModel.title,
            text = domainModel.text,
            trashed = domainModel.trashed,
            created = System.currentTimeMillis(),
            archived = domainModel.archived,
            categoryId = domainModel.categoryId
        )
    }

    fun mapFromEntityList(initial: List<NoteEntity>): List<Note> {
        return initial.map { mapFromEntity(it) }
    }
}
