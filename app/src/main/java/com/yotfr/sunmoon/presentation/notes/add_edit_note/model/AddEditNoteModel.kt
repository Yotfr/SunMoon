package com.yotfr.sunmoon.presentation.notes.add_edit_note.model

data class AddEditNoteModel(
    val id: Long?= null,
    val importance:Boolean = false,
    val title:String = "",
    val text:String = "",
    val trashed:Boolean = false,
    val created: Long? = null,
    val archived:Boolean = false,
    val categoryId:Long? = null
)