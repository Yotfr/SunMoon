package com.yotfr.sunmoon.domain.interactor.note.use_case

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.domain.model.note.CategoryMapper
import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeCategoryVisibility(
    private val categoryRepository: CategoryRepository
) {
    private val categoryMapper = CategoryMapper()

    //change category visibility from noteList fragment
    suspend operator fun invoke(category: Category) {
        withContext(Dispatchers.IO) {
            categoryRepository.upsertCategory(
                categoryMapper.mapToEntity(
                    category.copy(
                        isVisible = !category.isVisible
                    )
                )
            )
        }
    }
}