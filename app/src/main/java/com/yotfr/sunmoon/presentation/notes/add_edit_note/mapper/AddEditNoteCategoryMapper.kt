package com.yotfr.sunmoon.presentation.notes.add_edit_note.mapper

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.presentation.notes.add_edit_note.model.AddEditNoteCategoryModel
import java.lang.IllegalArgumentException

class AddEditNoteCategoryMapper {
    fun fromDomain(domainModel: Category): AddEditNoteCategoryModel {
        return AddEditNoteCategoryModel(
            id = domainModel.categoryId,
            categoryDescription = domainModel.categoryDescription,
            isVisible = domainModel.isVisible,
            notesCount = domainModel.notes.size
        )
    }

    fun toDomain(uiModel: AddEditNoteCategoryModel): Category {
        return Category(
            categoryId = uiModel.id,
            categoryDescription = uiModel.categoryDescription,
            isVisible = uiModel.isVisible ?: throw IllegalArgumentException(
                "isVisisble field not found"
            ),
            notes = emptyList()
        )
    }

    fun fromDomainList(initial: List<Category>): List<AddEditNoteCategoryModel> {
        return initial.map { fromDomain(it) }
    }
}