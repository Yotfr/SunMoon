package com.yotfr.sunmoon.presentation.notes.archive_note.mapper

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.presentation.notes.archive_note.model.ArchiveNoteModel
import java.text.SimpleDateFormat
import java.util.*

class ArchiveNoteMapper {
    fun fromDomain(domainModel: Note, sdfPattern: String): ArchiveNoteModel {
        return ArchiveNoteModel(
            id = domainModel.noteId ?: throw IllegalArgumentException(
                "Not found noteId"
            ),
            isPinned = domainModel.isPinned,
            title = domainModel.title,
            text = domainModel.text,
            trashed = domainModel.trashed,
            archived = domainModel.archived,
            createdAt = formatTime(domainModel.created, sdfPattern),
            categoryId = domainModel.categoryId,
            createdAtLong = domainModel.created
        )
    }
    fun toDomain(uiModel: ArchiveNoteModel): Note {
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
    fun fromDomainList(initial: List<Note>, sdfPattern: String): List<ArchiveNoteModel> {
        return initial.map { fromDomain(it, sdfPattern) }
    }

    private fun formatTime(date: Long, sdfPattern: String): String {
        val sdf = SimpleDateFormat(sdfPattern, Locale.getDefault())
        return sdf.format(date)
    }
}
