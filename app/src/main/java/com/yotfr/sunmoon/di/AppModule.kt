package com.yotfr.sunmoon.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.yotfr.sunmoon.data.data_source.AppDataBase
import com.yotfr.sunmoon.data.repository.*
import com.yotfr.sunmoon.domain.interactor.note.*
import com.yotfr.sunmoon.domain.repository.task.SubTaskRepository
import com.yotfr.sunmoon.domain.repository.note.NoteRepository
import com.yotfr.sunmoon.domain.repository.task.TaskRepository
import com.yotfr.sunmoon.domain.interactor.task.*
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import com.yotfr.sunmoon.domain.repository.note.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext context: Context):DataStoreRepository {
        return DataStoreRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideAppDataBase(app: Application): AppDataBase {
        return Room.databaseBuilder(
            app,
            AppDataBase::class.java,
            "todo_db"
        ).build()
    }

    @Singleton
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

    @Singleton
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
            getAllRemindedTasks = GetAllRemindedTasks(taskRepository)
        )
    }

    @Singleton
    @Provides
    fun provideTaskRepository(db: AppDataBase): TaskRepository {
        return TaskRepositoryImpl(db.taskDao)
    }

    @Singleton
    @Provides
    fun provideSubTaskRepository(db: AppDataBase): SubTaskRepository {
        return SubTaskRepositoryImpl(db.subTaskDao)
    }

    @Singleton
    @Provides
    fun provideNoteRepository(db: AppDataBase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Singleton
    @Provides
    fun provideCategoryRepository(db: AppDataBase): CategoryRepository {
        return CategoryRepositoryImpl(db.categoryDao)
    }

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class ApplicationScope

}