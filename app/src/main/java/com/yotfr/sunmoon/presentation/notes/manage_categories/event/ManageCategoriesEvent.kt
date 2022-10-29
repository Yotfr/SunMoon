package com.yotfr.sunmoon.presentation.notes.manage_categories.event

import com.yotfr.sunmoon.presentation.notes.manage_categories.model.ManageCategoriesModel

sealed interface ManageCategoriesEvent {

    data class DeleteCategory(val category: ManageCategoriesModel): ManageCategoriesEvent

    data class ChangeCategoryVisibility(val category: ManageCategoriesModel): ManageCategoriesEvent

}