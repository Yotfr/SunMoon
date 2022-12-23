package com.yotfr.sunmoon.domain.model.note

data class Note(
    val noteId: Long?,
    val isPinned: Boolean,
    val title: String,
    val text: String,
    val trashed: Boolean,
    val created: Long,
    val archived: Boolean,
    val categoryId: Long?
)
