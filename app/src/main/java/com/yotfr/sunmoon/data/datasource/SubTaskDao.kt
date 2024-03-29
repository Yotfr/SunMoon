package com.yotfr.sunmoon.data.datasource

import androidx.room.*
import com.yotfr.sunmoon.data.datasource.entity.task.SubTaskEntity

@Dao
interface SubTaskDao {

    @Insert(entity = SubTaskEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTaskEntity: SubTaskEntity)

    @Delete(entity = SubTaskEntity::class)
    suspend fun deleteSubTask(subTaskEntity: SubTaskEntity)

    @Query("DELETE FROM subTask WHERE taskId =:taskId")
    suspend fun deleteAllRelatedSubTasks(taskId: Long)
}
