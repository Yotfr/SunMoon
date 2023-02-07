package com.yotfr.sunmoon.domain.usecase.note

import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAllTrashedNotes(
    private val notesRepository: NoteRepository
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            notesRepository.deleteAllTrashed()
        }
    }
}
