package com.yotfr.sunmoon.domain.model.note

data class Category(
    val categoryId: Long?,
    val categoryDescription: String,
    val isVisible: Boolean,
    val notes: List<Note>
)
