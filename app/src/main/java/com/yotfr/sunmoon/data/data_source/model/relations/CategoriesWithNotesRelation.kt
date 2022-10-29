package com.yotfr.sunmoon.data.data_source.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.yotfr.sunmoon.data.data_source.model.note.CategoryEntity
import com.yotfr.sunmoon.data.data_source.model.note.NoteEntity

data class CategoriesWithNotesRelation(
    @Embedded val categoryEntity: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val notes:List<NoteEntity>
)