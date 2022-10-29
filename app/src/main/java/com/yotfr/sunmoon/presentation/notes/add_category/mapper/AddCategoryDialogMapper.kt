package com.yotfr.sunmoon.presentation.notes.add_category.mapper

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.presentation.notes.add_category.model.AddCategoryDialogModel
import java.lang.IllegalArgumentException

class AddCategoryDialogMapper {
    fun fromDomain(domainModel: Category): AddCategoryDialogModel {
        return AddCategoryDialogModel(
            id = domainModel.categoryId,
            categoryDescription = domainModel.categoryDescription,
            isVisible = domainModel.isVisible
        )
    }
    fun toDomain(uiModel: AddCategoryDialogModel): Category {
        return Category(
            categoryId = uiModel.id,
            categoryDescription = uiModel.categoryDescription,
            isVisible = uiModel.isVisible ?: throw IllegalArgumentException(
                "isVisisble field not found"
            ),
            notes = emptyList()
        )
    }
    fun fromDomainList(initial:List<Category>):List<AddCategoryDialogModel>{
        return initial.map { fromDomain(it) }
    }
}