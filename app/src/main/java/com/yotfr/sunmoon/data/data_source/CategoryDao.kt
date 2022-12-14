package com.yotfr.sunmoon.data.data_source

import androidx.room.*
import com.yotfr.sunmoon.data.data_source.model.note.CategoryEntity
import com.yotfr.sunmoon.data.data_source.model.relations.CategoriesWithNotesRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(entity = CategoryEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(categoryEntity: CategoryEntity)

    @Delete(entity = CategoryEntity::class)
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Transaction
    @Query(value = "SELECT * FROM categoryentity")
    fun getAllCategories(): Flow<List<CategoriesWithNotesRelation>>

    @Transaction
    @Query(value = "SELECT * FROM categoryentity WHERE isVisible = 1")
    fun getAllVisibleCategories(): Flow<List<CategoriesWithNotesRelation>>

    @Transaction
    @Query(value = "SELECT * FROM categoryentity WHERE categoryId = :categoryId AND isVisible = 1")
    fun getCategoryWithNotes(categoryId: Long): Flow<CategoriesWithNotesRelation>

    @Transaction
    @Query(value = "SELECT * FROM categoryentity WHERE categoryId = :categoryId")
    fun getCategoryById(categoryId: Long): Flow<CategoriesWithNotesRelation?>
}
