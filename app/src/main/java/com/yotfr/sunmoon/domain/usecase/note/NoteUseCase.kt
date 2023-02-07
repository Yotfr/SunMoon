package com.yotfr.sunmoon.domain.usecase.note

data class NoteUseCase(
    val addCategory: AddCategory,
    val addNote: AddNote,
    val changeCategoryVisibility: ChangeCategoryVisibility,
    val deleteAllTrashedNotes: DeleteAllTrashedNotes,
    val deleteCategory: DeleteCategory,
    val deleteNote: DeleteNote,
    val getCategoryById: GetCategoryById,
    val getCategoryList: GetCategoryList,
    val getNoteById: GetNoteById,
    val getTrashedNoteList: GetTrashedNoteList,
    val trashUntrashNote: TrashUntrashNote,
    val getArchiveNotes: GetArchiveNotes,
    val getCategoryWithNotes: GetCategoryWithNotes,
    val changeArchiveNoteState: ChangeArchiveNoteState,
    val getVisibleCategoryList: GetVisibleCategoryList,
    val deleteAllUnarchivedNotes: DeleteAllUnarchivedNotes,
    val deleteAllArchivedNotes: DeleteAllArchivedNotes,
    val getAllNotes: GetAllNotes,
    val changeNotePinnedState: ChangeNotePinnedState
)
