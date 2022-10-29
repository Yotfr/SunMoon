package com.yotfr.sunmoon.data.data_source

import androidx.room.*
import com.yotfr.sunmoon.data.data_source.model.note.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = NoteEntity::class)
    suspend fun insertNote(noteEntity: NoteEntity):Long

    @Delete(entity = NoteEntity::class)
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query("SELECT * FROM noteentity WHERE trashed = 0 AND noteId = :id")
    fun findNoteById(id:Long): Flow<NoteEntity?>

    @Query("SELECT * FROM noteentity WHERE trashed = 1  AND title LIKE '%' || :searchQuery || '%' ORDER BY isPinned DESC, created")
    fun getTrashedUserNotes(searchQuery: String):Flow<List<NoteEntity>>

    @Query("SELECT * FROM noteentity WHERE archived = 0 AND title LIKE '%' || :searchQuery || '%' AND trashed = 0 ORDER BY isPinned DESC")
    fun getAllNotes(searchQuery: String):Flow<List<NoteEntity>>

    @Query("SELECT * FROM noteentity WHERE archived = 1 AND title LIKE '%' || :searchQuery || '%' AND trashed = 0")
    fun getArchiveNotes(searchQuery: String):Flow<List<NoteEntity>>

    @Query("SELECT * FROM noteentity WHERE categoryId = :categoryId")
    suspend fun getNotesByCategory(categoryId: Long): List<NoteEntity>

    @Query("DELETE FROM noteentity WHERE trashed = 0 AND archived = 0")
    suspend fun deleteAllUnarchivedNotes()

    @Query("DELETE FROM noteentity WHERE trashed = 0 AND archived = 1")
    suspend fun deleteAllArchivedNotes()

    @Query("DELETE FROM noteentity WHERE trashed = 1")
    suspend fun deleteAllTrashedNotes()

    @Query("SELECT noteId FROM noteentity WHERE rowId = :rowId ")
    suspend fun getIdByRowId(rowId:Long):Int
}