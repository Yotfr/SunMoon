package com.yotfr.sunmoon.data.data_source

import androidx.room.*
import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity

@Dao
interface SubTaskDao {

    @Insert(entity = SubTaskEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSubTask(subTaskEntity: SubTaskEntity)

    @Delete(entity = SubTaskEntity::class)
    suspend fun deleteSubTask(subTaskEntity: SubTaskEntity)

    @Query("DELETE FROM subTask WHERE taskId =:taskId")
    suspend fun deleteAllRelatedSubTasks(taskId: Long)
}
