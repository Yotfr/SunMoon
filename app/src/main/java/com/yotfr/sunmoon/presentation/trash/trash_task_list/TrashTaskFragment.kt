package com.yotfr.sunmoon.presentation.trash.trash_task_list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentTrashTaskBinding
import com.yotfr.sunmoon.presentation.utils.onQueryTextChanged
import com.yotfr.sunmoon.presentation.trash.trash_task_list.adapter.*
import com.yotfr.sunmoon.presentation.trash.trash_task_list.event.TrashTaskEvent
import com.yotfr.sunmoon.presentation.trash.trash_task_list.event.TrashTaskUiEvent
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class TrashTaskFragment : Fragment(R.layout.fragment_trash_task) {

    private val viewModel by viewModels<TrashTaskViewModel>()

    private lateinit var binding: FragmentTrashTaskBinding

    private lateinit var searchView: SearchView

    private lateinit var uncompletedTrashTaskAdapter: TrashedUncompletedTaskListAdapter
    private lateinit var completedTrashTaskAdapter: TrashedCompletedTaskAdapter
    private lateinit var completedTrashTaskHeaderAdapter: TrashedCompletedHeaderAdapter
    private lateinit var trashTaskFooterAdapter: TrashTaskFooterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrashTaskBinding.bind(view)

        //inflateMenu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.app_bar_menu_list, menu)

                val searchItem = menu.findItem(R.id.mi_action_search)
                searchView = searchItem.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery.isNotEmpty()) {
                    searchItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }

                searchView.onQueryTextChanged {
                    viewModel.onEvent(
                        TrashTaskEvent.UpdateSearchQuery(
                            searchQuery = it
                        )
                    )
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_delete_all_tasks -> {
                        showDeleteAllDialog {
                            when (it) {
                                DeleteOption.ALL_TRASHED -> {
                                    viewModel.onEvent(
                                        TrashTaskEvent.DeleteAllTrashedTask
                                    )

                                }
                                DeleteOption.COMPLETED_TRASHED -> {
                                    viewModel.onEvent(
                                        TrashTaskEvent.DeleteAllTrashedCompletedTask
                                    )
                                }
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        //initRvAdapters
        val linearLayoutManager = LinearLayoutManager(requireContext())
        uncompletedTrashTaskAdapter = TrashedUncompletedTaskListAdapter()

        completedTrashTaskAdapter = TrashedCompletedTaskAdapter()

        completedTrashTaskHeaderAdapter = TrashedCompletedHeaderAdapter()
        completedTrashTaskHeaderAdapter.attachDelegate(object : TrashedCompletedHeaderDelegate {
            override fun hideCompleted() {
                viewModel.onEvent(TrashTaskEvent.ChangeCompletedTasksVisibility)
            }
        })

        trashTaskFooterAdapter = TrashTaskFooterAdapter()

        val concatAdapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            uncompletedTrashTaskAdapter,
            completedTrashTaskHeaderAdapter,
            completedTrashTaskAdapter,
            trashTaskFooterAdapter
        )

        binding.fragmentTrashTaskRv.adapter = concatAdapter
        binding.fragmentTrashTaskRv.layoutManager = linearLayoutManager

        binding.fragmentTrashTaskRv.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )

        initSwipeToDelete()

        //collect uiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { taskState ->
                    taskState?.let { uiModel ->
                        completedTrashTaskAdapter.tasks = uiModel.completedTasks
                        uncompletedTrashTaskAdapter.deletedTasks = uiModel.uncompletedTasks
                        completedTrashTaskHeaderAdapter.headerState = uiModel.headerState
                        trashTaskFooterAdapter.footerState = uiModel.footerState
                    }
                }
            }
        }

        //collect uiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        TrashTaskUiEvent.ShowRestoreSnackbar -> {
                            showRestoreTaskSnackbar()
                        }
                        is TrashTaskUiEvent.ShowUndoDeleteSnackbar -> {
                            showUndoDeleteTaskSnackBar {
                                viewModel.onEvent(
                                    TrashTaskEvent.UndoDeleteTrashedTask(
                                        uiEvent.task
                                    )
                                )
                            }
                        }
                        is TrashTaskUiEvent.ShowDateTimeChangeDialog -> {
                            showDateTimeChangeDialog(
                                onNeutral = {
                                    viewModel.onEvent(
                                        TrashTaskEvent.RestoreTrashedTaskWithDateTimeChanged(
                                            task = uiEvent.task,
                                            date = uiEvent.task.scheduledDate,
                                            time = uiEvent.task.scheduledTime
                                        )
                                    )
                                },
                                onNegative = {
                                    viewModel.onEvent(
                                        TrashTaskEvent.RestoreTrashedTaskWithDateTimeChanged(
                                            task = uiEvent.task,
                                            date = null,
                                            time = null
                                        )
                                    )
                                },
                                onPositive = {
                                    showDateTimePicker(
                                        viewModel.timeFormat.value
                                    ){ selectedDate, selectedTime ->
                                       viewModel.onEvent(
                                           TrashTaskEvent.RestoreTrashedTaskWithDateTimeChanged(
                                               task = uiEvent.task,
                                               date = selectedDate,
                                               time = selectedTime
                                           )
                                       )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    //show date time change dialog in case restoring deleted task is outdated
    private fun showDateTimeChangeDialog(
        onNeutral: () -> Unit,
        onNegative: () -> Unit,
        onPositive: () -> Unit
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.trash_need_change))
            .setNeutralButton(resources.getString(R.string.NO)) { _, _ ->
                onNeutral()
            }
            .setNegativeButton(resources.getString(R.string.make_unplanned)) { _, _ ->
                onNegative()
            }
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                onPositive()
            }.show()
    }

    private fun showDateTimePicker(
        currentTimeFormat: Int,
        onResult: (
            selectedDate: Long?, selectedTime: Long?
        ) -> Unit
    ) {
        val calendarDate = Calendar.getInstance(Locale.getDefault())
        var selectedDate: Long?
        var selectedTime: Long? = null
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        datePicker.show(parentFragmentManager, "tag")
        datePicker.addOnPositiveButtonClickListener { date ->
            calendarDate.timeInMillis = date
            calendarDate.set(Calendar.HOUR_OF_DAY, 0)
            calendarDate.set(Calendar.MINUTE, 0)
            calendarDate.set(Calendar.SECOND, 0)
            calendarDate.set(Calendar.MILLISECOND, 0)

            selectedDate = calendarDate.timeInMillis
            val calendarTime = Calendar.getInstance(Locale.getDefault())
            val currentHour = calendarTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendarTime.get(Calendar.MINUTE)
            val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(activity)

            val timeFormat = if (currentTimeFormat != 0) {
                currentTimeFormat
            } else if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(timeFormat)
                .setHour(currentHour)
                .setMinute(currentMinute)
                .setTitleText(getString(R.string.select_time))
                .build()
            picker.show(parentFragmentManager, "tag")
            picker.addOnPositiveButtonClickListener {
                calendarTime.set(0, 0, 0, picker.hour, picker.minute)
                calendarTime.set(Calendar.MILLISECOND, 0)
                selectedTime = calendarTime.timeInMillis
                onResult(
                    selectedDate,
                    selectedTime
                )
            }
            picker.addOnNegativeButtonClickListener {
                onResult(
                    selectedDate,
                    selectedTime
                )
            }
            picker.addOnCancelListener {
                onResult(
                    selectedDate,
                    selectedTime
                )
            }
            picker.addOnDismissListener {
                onResult(
                    selectedDate,
                    selectedTime
                )
            }
        }
    }

    private fun showDeleteAllDialog(onPositive: (deleteOption: DeleteOption) -> Unit) {
        val dialogOptions = arrayOf(
            resources.getString(R.string.all_trashed_completed),
            resources.getString(R.string.all_trashed)
        )
        val checkedItem = 0
        var selectedItem = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.choose_delete_options))
            .setSingleChoiceItems(dialogOptions, checkedItem) { _, which ->
                selectedItem = which
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                when (selectedItem) {
                    0 -> onPositive(DeleteOption.COMPLETED_TRASHED)
                    else -> onPositive(DeleteOption.ALL_TRASHED)
                }
            }.show()
    }

    private fun showUndoDeleteTaskSnackBar(onAction: () -> Unit) {
        Snackbar.make(
            requireView(),
            getString(R.string.task_deleted),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo_delete_task_button_text)) {
                onAction()
            }.show()
    }

    private fun showRestoreTaskSnackbar() {
        Snackbar.make(
            requireView(),
            getString(R.string.task_restored),
            Snackbar.LENGTH_LONG
        ).show()
    }

    //initialize itemTouchCallback
    private fun initSwipeToDelete() {
        val onUncompletedItemRemoved = { positionToRemove: Int ->
            val task = uncompletedTrashTaskAdapter.deletedTasks[positionToRemove]
            viewModel.onEvent(
                TrashTaskEvent.DeleteTrashedTask(
                    task = task
                )
            )
        }
        val onCompletedItemRemoved = { positionToRemove: Int ->
            val task = completedTrashTaskAdapter.tasks[positionToRemove]
            viewModel.onEvent(
                TrashTaskEvent.DeleteTrashedTask(
                    task = task
                )
            )
        }
        val onUncompletedItemRestored = { positionToRemove: Int ->
            val task = uncompletedTrashTaskAdapter.deletedTasks[positionToRemove]
            viewModel.onEvent(
                TrashTaskEvent.RestoreTrashedTask(
                    task = task
                )
            )
        }
        val onCompletedItemRestored = { positionToRemove: Int ->
            val task = completedTrashTaskAdapter.tasks[positionToRemove]
            viewModel.onEvent(
                TrashTaskEvent.RestoreTrashedTask(
                    task = task
                )
            )
        }
        val trashedTaskListItemCallback = TrashedTaskListItemCallback(
            onUncompletedItemDelete = onUncompletedItemRemoved,
            onUncompletedItemRestore = onUncompletedItemRestored,
            onCompletedItemDelete = onCompletedItemRemoved,
            onCompletedItemRestore = onCompletedItemRestored
        )
        ItemTouchHelper(trashedTaskListItemCallback).attachToRecyclerView(binding.fragmentTrashTaskRv)
    }

    //enum for delete dialog options
    enum class DeleteOption {
        ALL_TRASHED, COMPLETED_TRASHED
    }
}