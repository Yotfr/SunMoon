package com.yotfr.sunmoon.data.repository

import com.yotfr.sunmoon.data.datasource.CategoryDao
import com.yotfr.sunmoon.data.datasource.entity.note.CategoryEntity
import com.yotfr.sunmoon.data.datasource.entity.relations.CategoriesWithNotesRelation
import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun upsertCategory(categoryEntity: CategoryEntity) {
        categoryDao.insertCategory(categoryEntity)
    }

    override suspend fun deleteCategory(categoryEntity: CategoryEntity) {
        categoryDao.deleteCategory(categoryEntity)
    }

    override fun getCategoryWithNotes(categoryId: Long): Flow<CategoriesWithNotesRelation> {
        return categoryDao.getCategoryWithNotes(categoryId)
    }

    override fun getCategoryById(categoryId: Long): Flow<CategoriesWithNotesRelation?> {
        return categoryDao.getCategoryById(categoryId)
    }

    override fun getAllCategories(): Flow<List<CategoriesWithNotesRelation>> {
        return categoryDao.getAllCategories()
    }

    override fun getAllVisibleCategory(): Flow<List<CategoriesWithNotesRelation>> {
        return categoryDao.getAllVisibleCategories()
    }
}
