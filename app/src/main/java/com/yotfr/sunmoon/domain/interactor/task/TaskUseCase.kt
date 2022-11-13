package com.yotfr.sunmoon.domain.interactor.task

import com.yotfr.sunmoon.domain.interactor.task.use_case.*

data class TaskUseCase(
    val addSubTask: AddSubTask,
    val addTask: AddTask,
    val changeSubTaskCompletionState: ChangeSubTaskCompletionState,
    val changeTaskCompletionState: ChangeTaskCompletionState,
    val deleteAllTrashedTasksUseCase: DeleteAllTrashedTasksUseCase,
    val deleteTaskUseCase: DeleteTaskUseCase,
    val getTaskById: GetTaskById,
    val getScheduledTaskList: GetScheduledTaskList,
    val getUnplannedTaskList: GetUnplannedTaskList,
    val getOutdatedTaskList: GetOutdatedTaskList,
    val getTrashedTaskListUseCase: GetTrashedTaskListUseCase,
    val trashUntrashTask: TrashUntrashTask,
    val deleteSubTask: DeleteSubTask,
    val deleteScheduledTasks: DeleteScheduledTasks,
    val deleteUnplanned: DeleteUnplanned,
    val deleteOutdated: DeleteOutdated,
    val deleteScheduledTasksForSelectedDate: DeleteScheduledTasksForSelectedDate,
    val changeTaskScheduledTime: ChangeTaskScheduledTime,
    val changeTaskScheduledDate: ChangeTaskScheduledDate,
    val changeTaskScheduledDateTime: ChangeTaskScheduledDateTime,
    val changeSubTaskText: ChangeSubTaskText,
    val deleteAllCompletedTrashedTasksUseCase: DeleteAllCompletedTrashedTasksUseCase,
    val deleteAllScheduledCompletedTasks: DeleteAllScheduledCompletedTasks,
    val deleteScheduledCompletedTasksForSelectedDateUseCase: DeleteScheduledCompletedTasksForSelectedDate,
    val deleteAllUnplannedCompletedTasks: DeleteAllUnplannedCompletedTasks,
    val deleteAllOutdatedCompletedTasks: DeleteAllOutdatedCompletedTasks,
    val changeTaskRemindDateTime: ChangeTaskRemindDateTime,
    val changeTaskImportanceState: ChangeTaskImportanceState,
    val getAllRemindedTasks: GetAllRemindedTasks,
    val deleteAllRelatedSubTasks: DeleteAllRelatedSubTasks
)
