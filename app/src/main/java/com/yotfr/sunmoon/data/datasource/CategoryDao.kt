package com.yotfr.sunmoon.data.datasource

import androidx.room.*
import com.yotfr.sunmoon.data.datasource.entity.note.CategoryEntity
import com.yotfr.sunmoon.data.datasource.entity.relations.CategoriesWithNotesRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(entity = CategoryEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity)

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
