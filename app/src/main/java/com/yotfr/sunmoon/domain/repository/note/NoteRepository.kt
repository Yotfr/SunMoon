package com.yotfr.sunmoon.domain.repository.note

import com.yotfr.sunmoon.data.datasource.entity.note.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun upsertNote(noteEntity: NoteEntity): Long

    suspend fun deleteNote(noteEntity: NoteEntity)

    suspend fun findNoteById(id: Long): Flow<NoteEntity?>

    fun getTrashedNotes(searchQuery: String): Flow<List<NoteEntity>>

    suspend fun getNotesByCategory(categoryId: Long): List<NoteEntity>

    suspend fun deleteAllTrashed()

    fun getAllNotes(searchQuery: String): Flow<List<NoteEntity>>

    fun getArchiveNotes(searchQuery: String): Flow<List<NoteEntity>>

    suspend fun getIdByRowId(rowId: Long): Int

    suspend fun deleteAllUnarchivedNotes()

    suspend fun deleteAllArchivedNotes()
}
