package com.yotfr.sunmoon.domain.repository.note

import com.yotfr.sunmoon.data.datasource.entity.note.CategoryEntity
import com.yotfr.sunmoon.data.datasource.entity.relations.CategoriesWithNotesRelation
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun upsertCategory(categoryEntity: CategoryEntity)

    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    fun getCategoryWithNotes(categoryId: Long): Flow<CategoriesWithNotesRelation>

    fun getCategoryById(categoryId: Long): Flow<CategoriesWithNotesRelation?>

    fun getAllCategories(): Flow<List<CategoriesWithNotesRelation>>

    fun getAllVisibleCategory(): Flow<List<CategoriesWithNotesRelation>>
}
