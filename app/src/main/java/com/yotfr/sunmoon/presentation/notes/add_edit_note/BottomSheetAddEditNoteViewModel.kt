package com.yotfr.sunmoon.presentation.notes.add_edit_note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.note.NoteUseCase
import com.yotfr.sunmoon.presentation.notes.add_edit_note.event.AddEditNoteEvent
import com.yotfr.sunmoon.presentation.notes.add_edit_note.event.AddEditNoteUiEvent
import com.yotfr.sunmoon.presentation.notes.add_edit_note.mapper.AddEditNoteCategoryMapper
import com.yotfr.sunmoon.presentation.notes.add_edit_note.mapper.AddEditNoteMapper
import com.yotfr.sunmoon.presentation.notes.add_edit_note.model.AddEditNoteCategoryModel
import com.yotfr.sunmoon.presentation.notes.add_edit_note.model.AddEditNoteModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetAddEditNoteViewModel @Inject constructor(
    state: SavedStateHandle,
    private val noteUseCase: NoteUseCase
) : ViewModel() {

    private val addEditNoteMapper = AddEditNoteMapper()
    private val addEditNoteCategoryMapper = AddEditNoteCategoryMapper()

    private val noteId = state.get<Long>("noteId")
    private val categoryId = state.get<Long>("categoryId")

    private val _addEditNoteUiState = MutableStateFlow(AddEditNoteModel())
    val addEditNoteUiState = _addEditNoteUiState.asStateFlow()

    private val _addEditNoteCategoryUiState =
        MutableStateFlow<MutableList<AddEditNoteCategoryModel>?>(null)
    val addEditNoteCategoryUiState = _addEditNoteCategoryUiState.asStateFlow()

    private val _uiStateSelectedCategory = MutableStateFlow<AddEditNoteCategoryModel?>(null)
    val uiStateSelectedCategory = _uiStateSelectedCategory.asStateFlow()

    private val _uiEvent = Channel<AddEditNoteUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        if (noteId != BottomSheetAddEditNoteFragment.WITHOUT_NOTE_ID) {
            viewModelScope.launch {
                noteUseCase.getNoteById(
                    noteId = noteId!!
                ).collect { note ->
                    _addEditNoteUiState.value = addEditNoteMapper.fromDomain(
                        note
                    )
                    if (note.categoryId != null) {
                        noteUseCase.getCategoryById(
                            categoryId = note.categoryId
                        ).collectLatest { category ->
                            _uiStateSelectedCategory.value = addEditNoteCategoryMapper.fromDomain(
                                category
                            )
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            noteUseCase.getCategoryList().collect { categories ->
                _addEditNoteCategoryUiState.value = addEditNoteCategoryMapper.fromDomainList(
                    categories
                ).toMutableList()
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.SaveNotePressed -> {
                viewModelScope.launch {
                    noteUseCase.addNote(
                        note = addEditNoteMapper.toDomain(
                            _addEditNoteUiState.value.copy(
                                title = event.title,
                                text = event.text,
                                categoryId = _uiStateSelectedCategory.value?.id
                            )
                        )
                    )
                }
                sendToUi(AddEditNoteUiEvent.PopBackStack)
            }
            is AddEditNoteEvent.ChangeSelectedCategory -> {
                _uiStateSelectedCategory.value = event.newSelectedCategory
            }
            AddEditNoteEvent.ClearSelectedCategory -> {
                _uiStateSelectedCategory.value = null
            }
        }
    }

    private fun sendToUi(event: AddEditNoteUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


}
