package com.yotfr.sunmoon.domain.interactor.note.use_case

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.domain.model.note.NoteMapper
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class GetNoteById(
    private val noteRepository: NoteRepository
) {
    private val noteMapper = NoteMapper()

    suspend operator fun invoke(noteId: Long): Flow<Note> =
        withContext(Dispatchers.IO) {
            noteRepository.findNoteById(noteId).map { note ->
                noteMapper.mapFromEntity(
                    note ?: throw IllegalArgumentException(
                        "Can't found note by its id"
                    )
                )
            }
        }
}