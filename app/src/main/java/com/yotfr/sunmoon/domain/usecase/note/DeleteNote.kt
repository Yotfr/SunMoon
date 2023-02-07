package com.yotfr.sunmoon.domain.usecase.note

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.domain.model.note.NoteMapper
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteNote(
    private val noteRepository: NoteRepository
) {
    private val noteMapper = NoteMapper()

    suspend operator fun invoke(note: Note) {
        withContext(Dispatchers.IO) {
            noteRepository.deleteNote(noteMapper.mapToEntity(note))
        }
    }
}
