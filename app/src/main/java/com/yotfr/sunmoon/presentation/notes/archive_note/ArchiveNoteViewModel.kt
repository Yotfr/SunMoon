package com.yotfr.sunmoon.presentation.notes.archive_note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.note.NoteUseCase
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.notes.archive_note.event.ArchiveNoteEvent
import com.yotfr.sunmoon.presentation.notes.archive_note.event.ArchiveNoteUiEvent
import com.yotfr.sunmoon.presentation.notes.archive_note.mapper.ArchiveNoteMapper
import com.yotfr.sunmoon.presentation.notes.archive_note.model.ArchiveNoteFooterModel
import com.yotfr.sunmoon.presentation.notes.archive_note.model.ArchiveNoteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveNoteViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase,
    dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val archiveNoteListMapper = ArchiveNoteMapper()

    // state for search view
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<ArchiveNoteUiState?>(null)
    val uiState = _uiState.asStateFlow()

    // uiEvents channel
    private val _uiEvent = Channel<ArchiveNoteUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        // collect archive notes
        viewModelScope.launch {
            combine(
                dataStoreRepository.getDateFormat(),
                noteUseCase.getArchiveNotes(_searchQuery)
            ) { dateFormat, notes ->
                Pair(dateFormat, notes)
            }.collect {
                _uiState.value = ArchiveNoteUiState(
                    notes = archiveNoteListMapper.fromDomainList(
                        it.second,
                        it.first
                    ),
                    footerState = ArchiveNoteFooterModel(
                        isVisible = it.second.isEmpty()
                    )
                )
            }
        }
    }

    // method for fragment to communicate with viewModel
    fun onEvent(event: ArchiveNoteEvent) {
        when (event) {
            is ArchiveNoteEvent.DeleteArchiveNote -> {
                viewModelScope.launch {
                    noteUseCase.trashUntrashNote(
                        archiveNoteListMapper.toDomain(
                            event.note
                        )
                    )
                }
                sendToUi(
                    ArchiveNoteUiEvent.ShowUndoDeleteSnackbar(
                        note = event.note.copy(
                            trashed = true
                        )
                    )
                )
            }
            is ArchiveNoteEvent.UndoDeleteArchiveNote -> {
                viewModelScope.launch {
                    noteUseCase.trashUntrashNote(
                        archiveNoteListMapper.toDomain(
                            event.note
                        )
                    )
                }
            }
            is ArchiveNoteEvent.UnarchiveArchiveNote -> {
                viewModelScope.launch {
                    noteUseCase.changeArchiveNoteState(
                        archiveNoteListMapper.toDomain(
                            event.note
                        )
                    )
                }
                sendToUi(ArchiveNoteUiEvent.ShowUnarchiveSnackbar)
            }
            is ArchiveNoteEvent.UpdateSearchQuery -> {
                _searchQuery.value = event.searchQuery
            }
            is ArchiveNoteEvent.DeleteAllUnarchivedNote -> {
                viewModelScope.launch {
                    noteUseCase.deleteAllArchivedNotes()
                }
            }
        }
    }

    // send uiEvents to uiEvent channel
    private fun sendToUi(event: ArchiveNoteUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}
