package com.yotfr.sunmoon.presentation.notes.archive_note.model

data class ArchiveNoteModel(
    val id: Long,
    val isPinned:Boolean,
    val title:String,
    val text:String,
    val trashed:Boolean,
    val createdAt:String,
    val createdAtLong:Long,
    val archived:Boolean,
    val categoryId:Long?
)