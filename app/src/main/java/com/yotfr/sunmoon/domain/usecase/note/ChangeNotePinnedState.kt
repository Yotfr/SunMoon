package com.yotfr.sunmoon.domain.usecase.note

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.domain.model.note.NoteMapper
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeNotePinnedState(
    private val noteRepository: NoteRepository
) {
    private val noteMapper = NoteMapper()

    // pin/unpin note
    suspend operator fun invoke(note: Note) {
        withContext(Dispatchers.IO) {
            noteRepository.upsertNote(
                noteMapper.mapToEntity(
                    note.copy(
                        isPinned = !note.isPinned
                    )
                )
            )
        }
    }
}
