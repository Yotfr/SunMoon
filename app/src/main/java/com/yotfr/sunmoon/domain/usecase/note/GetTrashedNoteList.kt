package com.yotfr.sunmoon.domain.usecase.note

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.domain.model.note.NoteMapper
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetTrashedNoteList(
    private val noteRepository: NoteRepository
) {
    private val noteMapper = NoteMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        searchQuery: Flow<String>
    ): Flow<List<Note>> {
        return searchQuery.flatMapLatest {
            withContext(Dispatchers.IO) {
                noteRepository.getTrashedNotes(it).map { notes ->
                    noteMapper.mapFromEntityList(notes)
                }
            }
        }
    }
}