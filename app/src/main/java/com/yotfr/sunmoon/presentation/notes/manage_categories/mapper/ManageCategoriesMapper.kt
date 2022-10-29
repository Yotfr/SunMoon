package com.yotfr.sunmoon.presentation.notes.manage_categories.mapper

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.presentation.notes.manage_categories.model.ManageCategoriesModel

class ManageCategoriesMapper {

    fun fromDomain(domainModel: Category): ManageCategoriesModel {
        return ManageCategoriesModel(
            id = domainModel.categoryId ?: throw IllegalArgumentException(
                "Not found categoryId"
            ),
            categoryDescription = domainModel.categoryDescription,
            isVisible = domainModel.isVisible,
            notesCount = domainModel.notes.size
        )
    }
    fun toDomain(uiModel: ManageCategoriesModel): Category {
        return Category(
            categoryId = uiModel.id,
            categoryDescription = uiModel.categoryDescription,
            isVisible = uiModel.isVisible,
            notes = emptyList()
        )
    }
    fun fromDomainList(initial:List<Category>):List<ManageCategoriesModel>{
        return initial.map { fromDomain(it) }
    }
}