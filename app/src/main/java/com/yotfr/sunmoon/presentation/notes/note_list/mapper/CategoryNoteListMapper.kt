package com.yotfr.sunmoon.presentation.notes.note_list.mapper

import com.yotfr.sunmoon.domain.model.note.Category
import com.yotfr.sunmoon.presentation.notes.note_list.model.CategoryNoteListModel

class CategoryNoteListMapper {

    private val noteListMapper = NoteListMapper()

    fun fromDomain(domainModel: Category, sdfPattern: String): CategoryNoteListModel {
        return CategoryNoteListModel(
            id = domainModel.categoryId ?: throw IllegalArgumentException(
                "Not found categoryId"
            ),
            categoryDescription = domainModel.categoryDescription,
            isVisible = domainModel.isVisible,
            notes = noteListMapper.fromDomainList(domainModel.notes, sdfPattern)
        )
    }
    fun toDomain(uiModel: CategoryNoteListModel): Category {
        return Category(
            categoryId = uiModel.id,
            categoryDescription = uiModel.categoryDescription,
            isVisible = uiModel.isVisible,
            notes = emptyList()
        )
    }
    fun fromDomainList(initial: List<Category>, sdfPattern: String): List<CategoryNoteListModel> {
        return initial.map { fromDomain(it, sdfPattern) }
    }
}
