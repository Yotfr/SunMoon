package com.yotfr.sunmoon.data.data_source

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.yotfr.sunmoon.data.data_source.model.note.CategoryEntity
import com.yotfr.sunmoon.data.data_source.model.relations.CategoriesWithNotesRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = REPLACE, entity = CategoryEntity::class)
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Delete(entity = CategoryEntity::class)
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM categoryentity")
    fun getAllCategories():Flow<List<CategoriesWithNotesRelation>>

    @Query("SELECT * FROM categoryentity WHERE isVisible = 1")
    fun getAllVisibleCategories():Flow<List<CategoriesWithNotesRelation>>

    @Transaction
    @Query("SELECT * FROM categoryentity WHERE categoryId = :categoryId AND isVisible = 1")
    fun getCategoryWithNotes(categoryId: Long):Flow<CategoriesWithNotesRelation>

    @Transaction
    @Query("SELECT * FROM categoryentity WHERE categoryId = :categoryId")
    fun getCategoryById(categoryId:Long):Flow<CategoriesWithNotesRelation?>
}