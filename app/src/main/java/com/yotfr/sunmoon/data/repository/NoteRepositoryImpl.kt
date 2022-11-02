package com.yotfr.sunmoon.data.repository

import com.yotfr.sunmoon.data.data_source.NoteDao
import com.yotfr.sunmoon.data.data_source.model.note.NoteEntity
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {
    override suspend fun insertNote(noteEntity: NoteEntity): Long {
        return noteDao.insertNote(noteEntity)
    }

    override suspend fun deleteNote(noteEntity: NoteEntity) {
        noteDao.deleteNote(noteEntity)
    }

    override suspend fun findNoteById(id: Long): Flow<NoteEntity?> {
        return noteDao.findNoteById(id)
    }

    override fun getTrashedUserNotes(searchQuery: String): Flow<List<NoteEntity>> {
        return noteDao.getTrashedUserNotes(searchQuery)
    }

    override fun getAllNotes(searchQuery: String): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes(searchQuery)
    }

    override suspend fun deleteAllTrashed() {
        noteDao.deleteAllTrashedNotes()
    }

    override suspend fun getIdByRowId(rowId: Long): Int {
        return noteDao.getIdByRowId(rowId)
    }

    override fun getArchiveNotes(searchQuery: String): Flow<List<NoteEntity>> {
        return noteDao.getArchiveNotes(searchQuery)
    }

    override suspend fun deleteAllUnarchivedNotes() {
        return noteDao.deleteAllUnarchivedNotes()
    }

    override suspend fun deleteAllArchivedNotes() {
        return noteDao.deleteAllArchivedNotes()
    }

    override suspend fun getNotesByCategory(categoryId: Long): List<NoteEntity> {
        return noteDao.getNotesByCategory(categoryId)
    }
}