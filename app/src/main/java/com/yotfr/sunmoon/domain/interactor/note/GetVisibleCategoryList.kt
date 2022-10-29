package com.yotfr.sunmoon.domain.interactor.note

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.domain.model.note.CategoryMapper
import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetVisibleCategoryList(
    private val categoryRepository: CategoryRepository
) {
    private val categoryMapper = CategoryMapper()

    suspend operator fun invoke(): Flow<List<Category>> = withContext(Dispatchers.IO) {
        categoryRepository.getAllVisibleCategory().map { categoryMapper.mapFromEntityList(it) }
    }
}