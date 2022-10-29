package com.yotfr.sunmoon.presentation.notes.manage_categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.domain.interactor.note.NoteUseCase
import com.yotfr.sunmoon.presentation.notes.manage_categories.event.ManageCategoriesEvent
import com.yotfr.sunmoon.presentation.notes.manage_categories.event.ManageCategoriesUiEvent
import com.yotfr.sunmoon.presentation.notes.manage_categories.mapper.ManageCategoriesMapper
import com.yotfr.sunmoon.presentation.notes.manage_categories.model.ManageCategoriesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageCategoriesViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase
) : ViewModel() {

    private val manageCategoriesMapper = ManageCategoriesMapper()

    private val _uiState = MutableStateFlow<List<ManageCategoriesModel>?>(null)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ManageCategoriesUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            noteUseCase.getCategoryList().collect { categories ->
                _uiState.value = manageCategoriesMapper.fromDomainList(
                    categories
                )
            }
        }
    }


    fun onEvent(event: ManageCategoriesEvent) {
        when (event) {

            is ManageCategoriesEvent.DeleteCategory -> {
                viewModelScope.launch {
                    noteUseCase.deleteCategory(
                        category = manageCategoriesMapper.toDomain(
                            event.category
                        )
                    )
                }
            }

            is ManageCategoriesEvent.ChangeCategoryVisibility -> {
                viewModelScope.launch {
                    noteUseCase.changeCategoryVisibility(
                        category = manageCategoriesMapper.toDomain(
                            event.category
                        )
                    )
                }
            }

        }
    }

}