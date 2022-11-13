package com.yotfr.sunmoon.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity


@Dao
interface SubTaskDao {

    @Upsert(entity = SubTaskEntity::class)
    suspend fun upsertSubTask(subTaskEntity: SubTaskEntity)

    @Delete(entity = SubTaskEntity::class)
    suspend fun deleteSubTask(subTaskEntity: SubTaskEntity)

    @Query("DELETE FROM subTask WHERE taskId =:taskId")
    suspend fun deleteAllRelatedSubTasks(taskId:Long)

}