package com.yotfr.sunmoon.presentation.notes.add_edit_note.model

data class AddEditNoteCategoryModel(
    val id:Long? = null,
    val categoryDescription:String,
    val isVisible:Boolean ?= false,
    val notesCount:Int?
)