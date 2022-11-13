package com.yotfr.sunmoon.domain.interactor.note.use_case

import com.yotfr.sunmoon.domain.model.note.Note
import com.yotfr.sunmoon.domain.model.note.NoteMapper
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetArchiveNotes(
    private val noteRepository: NoteRepository
) {
   private val noteMapper = NoteMapper()

     @OptIn(ExperimentalCoroutinesApi::class)
     suspend operator fun invoke(searchQuery: MutableStateFlow<String>): Flow<List<Note>>  {
         return searchQuery.flatMapLatest {
             withContext(Dispatchers.IO) {
                 noteRepository.getArchiveNotes(it).map { noteEntityList ->
                     noteMapper.mapFromEntityList(noteEntityList)
                 }
             }
         }
     }
}