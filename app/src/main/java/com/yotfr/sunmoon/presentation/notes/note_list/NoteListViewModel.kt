package com.yotfr.sunmoon.presentation.notes.note_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.note.*
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.presentation.notes.note_list.event.NoteListEvent
import com.yotfr.sunmoon.presentation.notes.note_list.event.NoteListUiEvent
import com.yotfr.sunmoon.presentation.notes.note_list.mapper.CategoryNoteListMapper
import com.yotfr.sunmoon.presentation.notes.note_list.mapper.NoteListMapper
import com.yotfr.sunmoon.presentation.notes.note_list.model.CategoryNoteListModel
import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListFooterModel
import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListUiStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val noteListMapper = NoteListMapper()
    private val categoryNoteListMapper = CategoryNoteListMapper()

    private val _noteListUiState = MutableStateFlow<NoteListUiStateModel?>(null)
    val noteListUiState = _noteListUiState.asStateFlow()

    private val _categoryListUiState = MutableStateFlow<List<CategoryNoteListModel>?>(null)
    val categoryListUiState = _categoryListUiState.asSharedFlow()

    private val selectedCategoryId = MutableStateFlow(-1L)

    private val _uiEvent = Channel<NoteListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                dataStoreRepository.getDateFormat(),
                selectedCategoryId
            ) { dateFormat, selectedCategoryId ->
                Pair(dateFormat, selectedCategoryId)
            }.collectLatest {
                if (it.second == -1L) {
                    noteUseCase.getAllNotes(
                        searchQuery = _searchQuery
                    ).collect { notes ->
                        _noteListUiState.value = NoteListUiStateModel(
                            notes = noteListMapper.fromDomainList(
                                notes,
                                it.first ?: "yyyy/MM/dd"
                            ),
                            footerState = NoteListFooterModel(
                                isVisible = notes.isEmpty()
                            )
                        )
                    }
                } else {
                    noteUseCase.getCategoryWithNotes(
                        selectedCategoryId,
                        _searchQuery
                    ).collect { catWithNotes ->
                        _noteListUiState.value = NoteListUiStateModel(
                            notes = categoryNoteListMapper.fromDomain(
                                catWithNotes ?: throw IllegalArgumentException(
                                    "Not found category for selected chip"
                                ),
                                it.first ?: "yyyy/MM/dd"
                            ).notes,
                            footerState = NoteListFooterModel(
                                isVisible = catWithNotes.notes.isEmpty()
                            )
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            noteUseCase.getVisibleCategoryList().collect { categories ->
                _categoryListUiState.value =
                    categoryNoteListMapper.fromDomainList(
                        categories,
                        dataStoreRepository.getDateFormat().first() ?: "yyyy/MM/dd"
                    )
            }
        }
    }


    fun onEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCase.trashUntrashNote(
                        noteListMapper.toDomain(
                            event.note
                        )
                    )
                }
                sendToUi(
                    NoteListUiEvent.ShowUndoDeleteSnackbar(
                        event.note.copy(
                            trashed = true
                        )
                    )
                )
            }
            is NoteListEvent.UndoDeleteNote -> {
                viewModelScope.launch {
                    noteUseCase.trashUntrashNote(
                        noteListMapper.toDomain(
                            event.note
                        )
                    )
                }
            }

            is NoteListEvent.ChangeSelectedCategory -> {
                selectedCategoryId.value = event.selectedCategoryId
                Log.d("TEST", "selectedCategoryChanged -> ${selectedCategoryId.value}")
            }

            is NoteListEvent.ArchiveNote -> {
                viewModelScope.launch {
                    noteUseCase.changeArchiveNoteState(
                        noteListMapper.toDomain(
                            event.note
                        )
                    )
                }
                sendToUi(
                    NoteListUiEvent.ShowUndoArchiveSnackbar(
                        event.note.copy(
                            archived = true
                        )
                    )
                )
            }

            is NoteListEvent.UndoArchiveNote -> {
                viewModelScope.launch {
                    noteUseCase.changeArchiveNoteState(
                        noteListMapper.toDomain(
                            event.note
                        )
                    )
                }
            }
            is NoteListEvent.UpdateSearchQuery -> {
                _searchQuery.value = event.searchQuery
            }
            is NoteListEvent.DeleteAllUnarchivedNote -> {
                viewModelScope.launch {
                    noteUseCase.deleteAllUnarchivedNotes()
                }
            }
            is NoteListEvent.PinUnpinNote -> {
                viewModelScope.launch {
                    noteUseCase.changeNotePinnedState(
                        note = noteListMapper.toDomain(
                            event.note
                        )
                    )
                }
            }
        }
    }

    private fun sendToUi(event: NoteListUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}

