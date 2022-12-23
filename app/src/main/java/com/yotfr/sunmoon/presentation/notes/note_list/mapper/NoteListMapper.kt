package com.yotfr.sunmoon.presentation.notes.note_list.mapper

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListModel
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class NoteListMapper {

    fun fromDomain(domainModel: Note, sdfPattern: String): NoteListModel {
        return NoteListModel(
            id = domainModel.noteId ?: throw IllegalArgumentException(
                "Not found noteId"
            ),
            isPinned = domainModel.isPinned,
            title = domainModel.title,
            text = domainModel.text,
            trashed = domainModel.trashed,
            archived = domainModel.archived,
            createdAt = formatTime(domainModel.created, sdfPattern),
            createdAtLong = domainModel.created,
            categoryId = domainModel.categoryId
        )
    }
    fun toDomain(uiModel: NoteListModel): Note {
        return Note(
            noteId = uiModel.id,
            isPinned = uiModel.isPinned,
            title = uiModel.title,
            text = uiModel.text,
            trashed = uiModel.trashed,
            archived = uiModel.archived,
            created = uiModel.createdAtLong,
            categoryId = uiModel.categoryId
        )
    }
    fun fromDomainList(initial: List<Note>, sdfPattern: String): List<NoteListModel> {
        return initial.map { fromDomain(it, sdfPattern) }
    }

    private fun formatTime(date: Long, sdfPattern: String): String {
        val sdf = SimpleDateFormat(sdfPattern, Locale.getDefault())
        return sdf.format(date)
    }
}
