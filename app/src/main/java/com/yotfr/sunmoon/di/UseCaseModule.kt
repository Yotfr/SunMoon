package com.yotfr.sunmoon.di

import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import com.yotfr.sunmoon.domain.usecase.note.*
import com.yotfr.sunmoon.domain.usecase.task.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

    @Provides
    fun provideNoteUseCase(
        noteRepository: NoteRepository,
        categoryRepository: CategoryRepository
    ): NoteUseCase {
        return NoteUseCase(
            addCategory = AddCategory(categoryRepository),
            addNote = AddNote(
                noteRepository
            ),
            changeCategoryVisibility = ChangeCategoryVisibility(categoryRepository),
            deleteAllTrashedNotes = DeleteAllTrashedNotes(noteRepository),
            deleteCategory = DeleteCategory(
                categoryRepository,
                noteRepository
            ),
            deleteNote = DeleteNote(
                noteRepository
            ),
            getCategoryById = GetCategoryById(categoryRepository),
            getCategoryList = GetCategoryList(categoryRepository),
            getNoteById = GetNoteById(noteRepository),
            getTrashedNoteList = GetTrashedNoteList(noteRepository),
            trashUntrashNote = TrashUntrashNote(noteRepository),
            getCategoryWithNotes = GetCategoryWithNotes(categoryRepository),
            getArchiveNotes = GetArchiveNotes(noteRepository),
            changeArchiveNoteState = ChangeArchiveNoteState(noteRepository),
            getVisibleCategoryList = GetVisibleCategoryList(categoryRepository),
            deleteAllUnarchivedNotes = DeleteAllUnarchivedNotes(noteRepository),
            deleteAllArchivedNotes = DeleteAllArchivedNotes(noteRepository),
            getAllNotes = GetAllNotes(noteRepository),
            changeNotePinnedState = ChangeNotePinnedState(noteRepository)
        )
    }

    @Provides
    fun provideTaskUseCase(
        taskRepository: TaskRepository,
        subTaskRepository: SubTaskRepository
    ): TaskUseCase {
        return TaskUseCase(
            addSubTask = AddSubTask(subTaskRepository),
            addTask = AddTask(taskRepository),
            changeSubTaskCompletionState = ChangeSubTaskCompletionState(
                subTaskRepository
            ),
            changeTaskCompletionState = ChangeTaskCompletionState(taskRepository),
            deleteAllTrashedTasksUseCase = DeleteAllTrashedTasksUseCase(taskRepository),
            deleteTaskUseCase = DeleteTaskUseCase(taskRepository),
            getTaskById = GetTaskById(taskRepository),
            getScheduledTaskList = GetScheduledTaskList(taskRepository),
            getUnplannedTaskList = GetUnplannedTaskList(taskRepository),
            getOutdatedTaskList = GetOutdatedTaskList(taskRepository),
            getTrashedTaskListUseCase = GetTrashedTaskListUseCase(taskRepository),
            trashUntrashTask = TrashUntrashTask(taskRepository),
            deleteSubTask = DeleteSubTask(subTaskRepository),
            deleteScheduledTasks = DeleteScheduledTasks(taskRepository),
            deleteUnplanned = DeleteUnplanned(taskRepository),
            deleteOutdated = DeleteOutdated(taskRepository),
            deleteScheduledTasksForSelectedDate = DeleteScheduledTasksForSelectedDate(
                taskRepository
            ),
            changeTaskScheduledTime = ChangeTaskScheduledTime(taskRepository),
            changeTaskScheduledDateTime = ChangeTaskScheduledDateTime(taskRepository),
            changeTaskScheduledDate = ChangeTaskScheduledDate(taskRepository),
            changeSubTaskText = ChangeSubTaskText(subTaskRepository),
            deleteAllCompletedTrashedTasksUseCase = DeleteAllCompletedTrashedTasksUseCase(
                taskRepository
            ),
            deleteAllScheduledCompletedTasks = DeleteAllScheduledCompletedTasks(
                taskRepository
            ),
            deleteScheduledCompletedTasksForSelectedDateUseCase = DeleteScheduledCompletedTasksForSelectedDate(
                taskRepository
            ),
            deleteAllOutdatedCompletedTasks = DeleteAllOutdatedCompletedTasks(
                taskRepository
            ),
            deleteAllUnplannedCompletedTasks = DeleteAllUnplannedCompletedTasks(
                taskRepository
            ),
            changeTaskRemindDateTime = ChangeTaskRemindDateTime(
                taskRepository
            ),
            changeTaskImportanceState = ChangeTaskImportanceState(
                taskRepository
            ),
            getAllRemindedTasks = GetAllRemindedTasks(taskRepository),
            deleteAllRelatedSubTasks = DeleteAllRelatedSubTasks(subTaskRepository)
        )
    }
}
