package com.yotfr.sunmoon.presentation.task.add_subtask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.yotfr.sunmoon.di.AppModule
import com.yotfr.sunmoon.domain.usecase.task.TaskUseCase
import com.yotfr.sunmoon.presentation.task.add_subtask.event.AddSubTaskDialogEvent
import com.yotfr.sunmoon.presentation.task.add_subtask.mapper.AddSubTaskDialogMapper
import com.yotfr.sunmoon.presentation.task.add_subtask.model.AddSubTaskDialogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSubTaskDialogViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase,
    @AppModule.ApplicationScope private val applicationScope: CoroutineScope,
    state: SavedStateHandle
) : ViewModel() {

    private val addSubTaskDialogMapper = AddSubTaskDialogMapper()

    private val taskId = state.get<Long>("taskId")

    private val _addCategoryUiState = MutableStateFlow<AddSubTaskDialogModel?>(null)
    val uiState = _addCategoryUiState.asStateFlow()

    fun onEvent(event: AddSubTaskDialogEvent) {
        when (event) {
            is AddSubTaskDialogEvent.AddSubTask -> {
                if (event.subTaskText.isNotBlank() && event.subTaskText.isNotEmpty()) {
                    applicationScope.launch {
                        taskUseCase.addSubTask(
                            subTask = addSubTaskDialogMapper.toDomain(
                                AddSubTaskDialogModel(
                                    subTaskDescription = event.subTaskText,
                                    taskId = taskId ?: throw IllegalArgumentException(
                                        "Not found taskId"
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}
