package com.yotfr.sunmoon.data.data_source.model.note

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val noteId: Long?,
    val isPinned: Boolean,
    val title: String,
    val text: String,
    val trashed: Boolean,
    val created: Long,
    val archived: Boolean,
    val categoryId: Long?
)
