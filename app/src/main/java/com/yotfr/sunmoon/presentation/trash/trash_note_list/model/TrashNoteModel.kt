package com.yotfr.sunmoon.presentation.trash.trash_note_list.model

data class TrashNoteModel(
    val id: Long,
    val isPinned: Boolean,
    val title: String,
    val text: String,
    val trashed: Boolean,
    val createdAt: String,
    val createdAtLong: Long,
    val archived: Boolean,
    val categoryId: Long?
)
