package com.yotfr.sunmoon.domain.interactor.note

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.domain.model.note.CategoryMapper
import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetCategoryWithNotes(
    private val categoryRepository: CategoryRepository
) {
    private val categoryMapper = CategoryMapper()

    suspend operator fun invoke(
        selectedCategory: MutableStateFlow<Long>,
        searchQuery: MutableStateFlow<String>
    ): Flow<Category?> =
        withContext(Dispatchers.IO) {
            combine(
                selectedCategory,
                searchQuery
            ) { selectedCategory, searchQuery ->
                Pair(selectedCategory, searchQuery)
            }.flatMapLatest { (selectedCategory, searchQuery) ->
                categoryRepository.getCategoryWithNotes(selectedCategory).map { catWithNotes ->
                    val filteredNotes = catWithNotes.notes.filter { note ->
                        (note.title.contains(searchQuery) || note.text.contains(searchQuery))
                                &&!note.archived &&!note.trashed
                    }
                    val sortedNotes = filteredNotes.sortedByDescending { it.isPinned }
                    categoryMapper.mapFromEntity(
                        catWithNotes.copy(
                            categoryEntity = catWithNotes.categoryEntity,
                            notes = sortedNotes
                        )
                    )
                }
            }
        }
}
