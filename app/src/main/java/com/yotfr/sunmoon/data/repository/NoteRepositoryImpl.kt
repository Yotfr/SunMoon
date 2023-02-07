package com.yotfr.sunmoon.data.repository

import com.yotfr.sunmoon.data.datasource.NoteDao
import com.yotfr.sunmoon.data.datasource.entity.note.NoteEntity
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun upsertNote(noteEntity: NoteEntity): Long {
        return noteDao.insertNote(noteEntity)
    }

    override suspend fun deleteNote(noteEntity: NoteEntity) {
        noteDao.deleteNote(noteEntity)
    }

    override suspend fun findNoteById(id: Long): Flow<NoteEntity?> {
        return noteDao.findNoteById(id)
    }

    override fun getTrashedNotes(searchQuery: String): Flow<List<NoteEntity>> {
        return noteDao.getTrashedNotes(searchQuery)
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
