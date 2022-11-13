package com.yotfr.sunmoon.domain.interactor.note.use_case

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.domain.model.note.CategoryMapper
import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class DeleteCategory(
    private val categoryRepository: CategoryRepository,
    private val noteRepository: NoteRepository
) {
    private val categoryMapper = CategoryMapper()

    suspend operator fun invoke(category: Category) {
        withContext(Dispatchers.IO) {
            noteRepository.getNotesByCategory(
                categoryId = category.categoryId ?: throw IllegalArgumentException(
                    "Cannot find categoryId"
                )
            ).forEach { note ->
                //change notes selected category to "All" category
                noteRepository.upsertNote(
                    note.copy(
                        categoryId = null
                    )
                )
            }
            categoryRepository.deleteCategory(categoryMapper.mapToEntity(category))
        }
    }
}