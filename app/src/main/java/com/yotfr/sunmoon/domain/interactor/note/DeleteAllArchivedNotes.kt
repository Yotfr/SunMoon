package com.yotfr.sunmoon.domain.interactor.note

import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAllArchivedNotes(
    private val notesRepository: NoteRepository
) {
    suspend operator fun invoke(){
        withContext(Dispatchers.IO){
            notesRepository.deleteAllArchivedNotes()
        }
    }
}