package com.yotfr.sunmoon.presentation.notes.manage_categories.model

data class ManageCategoriesModel(
    val id:Long,
    val categoryDescription:String,
    val isVisible:Boolean,
    val notesCount:Int
)