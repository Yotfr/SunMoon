package com.yotfr.sunmoon.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import com.yotfr.sunmoon.data.data_source.model.task.SubTaskEntity


@Dao
interface SubTaskDao {

    @Insert(entity = SubTaskEntity::class, onConflict = REPLACE)
    suspend fun insertSubTask(subTaskEntity: SubTaskEntity)

    @Delete(entity = SubTaskEntity::class)
    suspend fun deleteSubTask(subTaskEntity: SubTaskEntity)

}