package com.yotfr.sunmoon.domain.interactor.note

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.domain.model.note.CategoryMapper
import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddCategory (
    private val categoryRepository: CategoryRepository
) {
    private val categoryMapper = CategoryMapper()

    suspend operator fun invoke(category:Category){
        withContext(Dispatchers.IO){
            categoryRepository.insertCategory(categoryMapper.mapToEntity(category))
        }
    }
}