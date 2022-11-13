package com.yotfr.sunmoon.domain.interactor.note.use_case

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.domain.model.note.NoteMapper
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetAllNotes(
    private val noteRepository: NoteRepository
) {
    private val noteMapper = NoteMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(searchQuery: MutableStateFlow<String>): Flow<List<Note>> {
        return searchQuery.flatMapLatest {
            withContext(Dispatchers.IO) {
                noteRepository.getAllNotes(it).map { noteEntityList ->
                    noteMapper.mapFromEntityList(noteEntityList)
                }
            }
        }
    }
}