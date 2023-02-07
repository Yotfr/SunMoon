package com.yotfr.sunmoon.presentation.notes.add_category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yotfr.sunmoon.di.AppModule
import com.yotfr.sunmoon.domain.usecase.note.NoteUseCase
import com.yotfr.sunmoon.presentation.notes.add_category.event.AddCategoryDialogEvent
import com.yotfr.sunmoon.presentation.notes.add_category.mapper.AddCategoryDialogMapper
import com.yotfr.sunmoon.presentation.notes.add_category.model.AddCategoryDialogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryDialogViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase,
    @AppModule.ApplicationScope private val applicationScope: CoroutineScope,
    state: SavedStateHandle
) : ViewModel() {

    private val addCategoryDialogMapper = AddCategoryDialogMapper()

    // state for categoryId coming from manageCategories
    private val categoryId = state.get<Long>("categoryId")

    // state for categoryDescription coming from manageCategories
    private val categoryDescription = state.get<String>("categoryDescription")

    // state for categoryDescription
    private val _addCategoryDescriptionState = MutableStateFlow("")
    val addCategoryDescriptionState = _addCategoryDescriptionState.asStateFlow()

    private val _addCategoryUiState = MutableStateFlow(AddCategoryDialogModel())

    init {
        // update categoryDescription state text and uiState if editing an existing category
        _addCategoryDescriptionState.value = categoryDescription ?: ""
        if (categoryId != AddCategoryDialogFragment.WITHOUT_CATEGORY_ID) {
            viewModelScope.launch {
                noteUseCase.getCategoryById(
                    categoryId = categoryId!!
                ).collect { category ->
                    _addCategoryUiState.value = addCategoryDialogMapper.fromDomain(
                        category
                    )
                }
            }
        }
    }

    // method for fragment to communicate with viewModel
    fun onEvent(event: AddCategoryDialogEvent) {
        when (event) {
            is AddCategoryDialogEvent.AddCategory -> {
                if (event.categoryText.isNotEmpty() && event.categoryText.isNotBlank()) {
                    applicationScope.launch {
                        noteUseCase.addCategory(
                            category = addCategoryDialogMapper.toDomain(
                                _addCategoryUiState.value.copy(
                                    categoryDescription = event.categoryText
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}
