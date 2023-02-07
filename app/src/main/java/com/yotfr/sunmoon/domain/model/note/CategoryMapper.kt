package com.yotfr.sunmoon.domain.model.note

import com.yotfr.sunmoon.data.datasource.entity.note.CategoryEntity
import com.yotfr.sunmoon.data.datasource.entity.relations.CategoriesWithNotesRelation

class CategoryMapper {

    private val noteMapper = NoteMapper()

    fun mapFromEntity(entity: CategoriesWithNotesRelation): Category {
        return Category(
            categoryId = entity.categoryEntity.categoryId,
            categoryDescription = entity.categoryEntity.categoryDescription,
            isVisible = entity.categoryEntity.isVisible,
            notes = noteMapper.mapFromEntityList(entity.notes)
        )
    }

    fun mapToEntity(domainModel: Category): CategoryEntity {
        return CategoryEntity(
            categoryId = domainModel.categoryId,
            categoryDescription = domainModel.categoryDescription,
            isVisible = domainModel.isVisible
        )
    }

    fun mapFromEntityList(initial: List<CategoriesWithNotesRelation>): List<Category> {
        return initial.map { mapFromEntity(it) }
    }
}
