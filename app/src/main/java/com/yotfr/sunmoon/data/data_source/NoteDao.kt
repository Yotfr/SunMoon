package com.yotfr.sunmoon.data.data_source

import androidx.room.*
import com.yotfr.sunmoon.data.data_source.model.note.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(entity = NoteEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNote(noteEntity: NoteEntity): Long

    @Delete(entity = NoteEntity::class)
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query(value = "SELECT * FROM noteentity WHERE trashed = 0 AND noteId = :id")
    fun findNoteById(id: Long): Flow<NoteEntity?>

    @Query(value = "SELECT * FROM noteentity WHERE trashed = 1  AND title LIKE '%' || :searchQuery || '%' ORDER BY isPinned DESC, created")
    fun getTrashedNotes(searchQuery: String): Flow<List<NoteEntity>>

    @Query(value = "SELECT * FROM noteentity WHERE archived = 0 AND title LIKE '%' || :searchQuery || '%' AND trashed = 0 ORDER BY isPinned DESC")
    fun getAllNotes(searchQuery: String): Flow<List<NoteEntity>>

    @Query(value = "SELECT * FROM noteentity WHERE archived = 1 AND title LIKE '%' || :searchQuery || '%' AND trashed = 0")
    fun getArchiveNotes(searchQuery: String): Flow<List<NoteEntity>>

    @Query(value = "SELECT * FROM noteentity WHERE categoryId = :categoryId")
    suspend fun getNotesByCategory(categoryId: Long): List<NoteEntity>

    @Query(value = "DELETE FROM noteentity WHERE trashed = 0 AND archived = 0")
    suspend fun deleteAllUnarchivedNotes()

    @Query(value = "DELETE FROM noteentity WHERE trashed = 0 AND archived = 1")
    suspend fun deleteAllArchivedNotes()

    @Query(value = "DELETE FROM noteentity WHERE trashed = 1")
    suspend fun deleteAllTrashedNotes()

    @Query(value = "SELECT noteId FROM noteentity WHERE rowId = :rowId ")
    suspend fun getIdByRowId(rowId: Long): Int
}
