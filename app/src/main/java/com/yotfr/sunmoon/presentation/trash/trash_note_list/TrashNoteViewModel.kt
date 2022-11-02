package com.yotfr.sunmoon.presentation.trash.trash_note_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.note.*
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.trash.trash_note_list.event.TrashNoteEvent
import com.yotfr.sunmoon.presentation.trash.trash_note_list.event.TrashNoteUiEvent
import com.yotfr.sunmoon.presentation.trash.trash_note_list.mapper.TrashNoteMapper
import com.yotfr.sunmoon.presentation.trash.trash_note_list.model.TrashNoteFooterModel
import com.yotfr.sunmoon.presentation.trash.trash_note_list.model.TrashNoteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashNoteViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val trashNoteMapper = TrashNoteMapper()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    private val _uiState = MutableStateFlow<TrashNoteUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<TrashNoteUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                dataStoreRepository.getDateFormat(),
                noteUseCase.getTrashedNoteList(
                    searchQuery = _searchQuery
                )
            ){ dateFormat, notes ->
                Pair(dateFormat,notes)
            }.collect{
                _uiState.value = TrashNoteUiState(
                    notes = it.second.map { note ->
                        trashNoteMapper.fromDomain(note, it.first ?: "yyyy/MM/dd")
                    },
                    footerState = TrashNoteFooterModel(
                        isVisible = it.second.isEmpty()
                    )
                )
            }
        }
    }


    fun onEvent(event: TrashNoteEvent) {
        when (event) {

            is TrashNoteEvent.DeleteTrashedNote -> {
                viewModelScope.launch {
                    noteUseCase.deleteNote(
                        note = trashNoteMapper.toDomain(
                            event.note
                        )
                    )
                }
                sendToUi(TrashNoteUiEvent.ShowUndoDeleteSnackbar(
                    note = event.note
                ))
            }
            is TrashNoteEvent.RestoreTrashedNote -> {
                viewModelScope.launch {
                    noteUseCase.trashUntrashNote(
                        note = trashNoteMapper.toDomain(
                            event.note
                        )
                    )
                }
                sendToUi(TrashNoteUiEvent.ShowRestoreSnackbar)
            }
            is TrashNoteEvent.UndoDeleteTrashedNote -> {
                viewModelScope.launch {
                    noteUseCase.addNote(
                        note = trashNoteMapper.toDomain(
                            event.note
                        )
                    )
                }
            }
            is TrashNoteEvent.UpdateSearchQuery -> {
                _searchQuery.value = event.searchQuery
            }
            TrashNoteEvent.DeleteAllTrashedNotes -> {
                viewModelScope.launch {
                    noteUseCase.deleteAllTrashedNotes()
                }
            }
        }
    }

    private fun sendToUi(event: TrashNoteUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}