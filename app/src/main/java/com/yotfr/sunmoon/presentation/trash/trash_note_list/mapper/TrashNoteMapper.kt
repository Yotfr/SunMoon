package com.yotfr.sunmoon.presentation.trash.trash_note_list.mapper

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.presentation.trash.trash_note_list.model.TrashNoteModel
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class TrashNoteMapper {
    fun fromDomain(domainModel: Note, sdfPattern: String): TrashNoteModel {
        return TrashNoteModel(
            id = domainModel.noteId ?: throw IllegalArgumentException(
                "Not found noteId"
            ),
            isPinned = domainModel.isPinned,
            title = domainModel.title,
            text = domainModel.text,
            trashed = domainModel.trashed,
            createdAt = formatDate(domainModel.created,sdfPattern),
            archived = domainModel.archived,
            categoryId = domainModel.categoryId,
            createdAtLong = domainModel.created
        )
    }
    fun toDomain(uiModel: TrashNoteModel): Note {
        return Note(
            noteId = uiModel.id,
            isPinned = uiModel.isPinned,
            title = uiModel.title,
            text = uiModel.text,
            trashed = uiModel.trashed,
            created = uiModel.createdAtLong,
            archived = uiModel.archived,
            categoryId = uiModel.categoryId
        )
    }
    fun fromDomainList(initial:List<Note>,sdfPattern: String):List<TrashNoteModel>{
        return initial.map { fromDomain(it,sdfPattern) }
    }

    private fun formatDate(date:Long,sdfPattern:String):String {
        val sdf = SimpleDateFormat(sdfPattern, Locale.getDefault())
        return sdf.format(date)
    }
}