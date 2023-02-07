package com.yotfr.sunmoon.data.datasource.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.yotfr.sunmoon.data.datasource.entity.note.CategoryEntity
import com.yotfr.sunmoon.data.datasource.entity.note.NoteEntity

data class CategoriesWithNotesRelation(
    @Embedded val categoryEntity: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val notes: List<NoteEntity>
)
