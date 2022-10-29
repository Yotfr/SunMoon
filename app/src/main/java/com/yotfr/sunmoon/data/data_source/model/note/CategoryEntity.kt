package com.yotfr.sunmoon.data.data_source.model.note

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId:Long?,
    val categoryDescription:String,
    val isVisible:Boolean
)