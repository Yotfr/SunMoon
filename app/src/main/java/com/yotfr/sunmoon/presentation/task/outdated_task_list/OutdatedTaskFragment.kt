package com.yotfr.sunmoon.presentation.task.outdated_task_list

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_12H
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.yotfr.sunmoon.AlarmReceiver
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentOutdatedTaskListBinding
import com.yotfr.sunmoon.presentation.task.TaskRootFragment
import com.yotfr.sunmoon.presentation.task.TaskRootFragmentDirections
import com.yotfr.sunmoon.presentation.task.outdated_task_list.adapter.*
import com.yotfr.sunmoon.presentation.task.outdated_task_list.event.OutdatedTaskEvent
import com.yotfr.sunmoon.presentation.task.outdated_task_list.event.OutdatedTaskUiEvent
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedDeleteOption
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListModel
import com.yotfr.sunmoon.presentation.task.task_details.TaskDetailsFragment
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import com.yotfr.sunmoon.presentation.utils.getColorFromAttr
import com.yotfr.sunmoon.presentation.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class OutdatedTaskFragment : Fragment(R.layout.fragment_outdated_task_list) {

    private var _binding: FragmentOutdatedTaskListBinding ? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<OutdatedTaskViewModel>()

    private var searchView: SearchView? = null

    private lateinit var outdatedUncompletedTaskAdapter: OutdatedUncompletedTaskAdapter
    private lateinit var outdatedCompletedTaskAdapter: OutdatedCompletedTaskAdapter
    private lateinit var outdatedCompletedHeaderAdapter: OutdatedCompletedHeaderAdapter
    private lateinit var outdatedFooterAdapter: OutdatedFooterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOutdatedTaskListBinding.bind(view)

        // inflateMenu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.app_bar_menu_list, menu)

                    val searchItem = menu.findItem(R.id.mi_action_search)
                    searchView = searchItem.actionView as SearchView

                    val etSearch =
                        searchView?.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
                    etSearch?.setTextColor(requireContext().getColorFromAttr(androidx.appcompat.R.attr.colorPrimary))

                    val pendingQuery = viewModel.searchQuery.value
                    if (pendingQuery.isNotEmpty()) {
                        searchItem.expandActionView()
                        searchView?.setQuery(pendingQuery, false)
                    }

                    searchView?.onQueryTextChanged {
                        viewModel.onEvent(
                            OutdatedTaskEvent.UpdateSearchQuery(
                                searchQuery = it
                            )
                        )
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.mi_delete_all_tasks -> {
                            showDeleteAllDialog { deleteOption ->
                                showConfirmationDialog {
                                    viewModel.onEvent(
                                        OutdatedTaskEvent.DeleteTasks(
                                            deleteOption = deleteOption
                                        )
                                    )
                                }
                            }
                            true
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        // initRvAdapters
        val layoutManager = LinearLayoutManager(requireContext())
        outdatedUncompletedTaskAdapter = OutdatedUncompletedTaskAdapter()
        outdatedUncompletedTaskAdapter.attachDelegate(object : OutdatedUncompletedTaskDelegate {
            override fun taskPressed(taskId: Long, transitionView: View) {
                val direction =
                    TaskRootFragmentDirections.actionTaskRootFragmentToTaskDetailsFragment(
                        taskId = taskId,
                        destination = TaskDetailsFragment.FROM_OUTDATED
                    )
                val extras = FragmentNavigatorExtras(
                    transitionView to
                        transitionView.transitionName
                )
                navigateToDestination(
                    direction = direction,
                    extras = extras
                )
            }

            override fun schedulePressed(task: OutdatedTaskListModel) {
                showDateTimePicker(
                    currentTimeFormat = viewModel.timeFormat.value
                ) { selectedDate, selectedTime ->
                    viewModel.onEvent(
                        OutdatedTaskEvent.ScheduleTask(
                            task = task,
                            date = selectedDate,
                            time = selectedTime
                        )
                    )
                }
            }
        })

        outdatedCompletedTaskAdapter = OutdatedCompletedTaskAdapter()
        outdatedCompletedTaskAdapter.attachDelegate(object : OutdatedCompletedTaskListDelegate {
            override fun taskPressed(taskId: Long, transitionView: View) {
                val direction =
                    TaskRootFragmentDirections.actionTaskRootFragmentToTaskDetailsFragment(
                        taskId = taskId,
                        destination = TaskDetailsFragment.FROM_OUTDATED
                    )
                val extras = FragmentNavigatorExtras(
                    transitionView to
                        transitionView.transitionName
                )
                navigateToDestination(
                    direction = direction,
                    extras = extras
                )
            }

            override fun schedulePressed(task: OutdatedTaskListModel) {
                showDateTimePicker(
                    currentTimeFormat = viewModel.timeFormat.value
                ) { selectedDate, selectedTime ->
                    viewModel.onEvent(
                        OutdatedTaskEvent.ScheduleTask(
                            task = task,
                            date = selectedDate,
                            time = selectedTime,
                            isMakeUndoneNeeded = true
                        )
                    )
                }
            }
        })

        outdatedCompletedHeaderAdapter = OutdatedCompletedHeaderAdapter()
        outdatedCompletedHeaderAdapter.attachDelegate(object : OutdatedCompletedHeaderDelegate {
            override fun hideCompleted() {
            }
        })

        outdatedFooterAdapter = OutdatedFooterAdapter()
        val concatAdapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            outdatedUncompletedTaskAdapter,
            outdatedCompletedHeaderAdapter,
            outdatedCompletedTaskAdapter,
            outdatedFooterAdapter
        )

        binding.rvOutdatedTask.adapter = concatAdapter
        binding.rvOutdatedTask.layoutManager = layoutManager

        binding.rvOutdatedTask.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )
        initSwipeToDelete()

        // collectUiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state?.let { uiModel ->
                        outdatedCompletedTaskAdapter.submitList(uiModel.completedTasks)
                        outdatedUncompletedTaskAdapter.submitList(uiModel.uncompletedTasks)
                        outdatedCompletedHeaderAdapter.headerState = uiModel.headerState
                        outdatedFooterAdapter.footerState = uiModel.footerState
                    }
                }
            }
        }

        // collectUiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is OutdatedTaskUiEvent.UndoDeleteOutdatedTask -> {
                            showUndoTrashSnackbar(
                                onAction = {
                                    viewModel.onEvent(
                                        OutdatedTaskEvent.UndoDeleteOutdatedTask(
                                            task = uiEvent.task
                                        )
                                    )
                                },
                                onDismiss = {
                                    cancelAlarm(uiEvent.task.taskId)
                                }
                            )
                        }
                        is OutdatedTaskUiEvent.NavigateToScheduledTask -> {
                            (parentFragment as TaskRootFragment).changeTabFromChildFragment(
                                fragmentPosition = 0,
                                selectedTaskDate = uiEvent.taskDate
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showUndoTrashSnackbar(
        onAction: () -> Unit,
        onDismiss: () -> Unit
    ) {
        Snackbar.make(
            requireView(),
            getString(R.string.task_trashed),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo)) {
                onAction()
            }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event == DISMISS_EVENT_TIMEOUT) {
                        onDismiss()
                    }
                }
            })
            .show()
    }

    private fun navigateToDestination(
        direction: NavDirections?,
        extras: FragmentNavigator.Extras
    ) {
        if (direction == null) {
            findNavController().popBackStack()
        } else {
            findNavController().navigate(direction, extras)
        }
    }

    // initialize itemCallback
    private fun initSwipeToDelete() {
        val onUncompletedItemTrashed = { positionToRemove: Int ->
            val task = outdatedUncompletedTaskAdapter.currentList[positionToRemove]
            viewModel.onEvent(
                OutdatedTaskEvent.TrashOutdatedTask(
                    task = task
                )
            )
        }
        val onCompletedItemTrashed = { positionToRemove: Int ->
            val task = outdatedCompletedTaskAdapter.currentList[positionToRemove]
            viewModel.onEvent(
                OutdatedTaskEvent.TrashOutdatedTask(
                    task = task
                )
            )
        }
        val outdatedTaskListItemCallback = OutdatedTaskListItemCallback(
            onUncompletedItemTrashed = onUncompletedItemTrashed,
            onCompletedItemTrashed = onCompletedItemTrashed
        )
        ItemTouchHelper(outdatedTaskListItemCallback).attachToRecyclerView(binding.rvOutdatedTask)
    }

    private fun showDateTimePicker(
        currentTimeFormat: Int,
        onResult: (
            selectedDate: Long,
            selectedTime: Long?
        ) -> Unit
    ) {
        val calendarDate = Calendar.getInstance(Locale.getDefault())
        var selectedDate: Long
        var selectedTime: Long? = null
        val endDateCalendar = calendarDate.clone() as Calendar
        endDateCalendar.add(Calendar.MONTH, 6)
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .setEnd(endDateCalendar.timeInMillis)
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
            val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(requireContext())

            val timeFormat = if (currentTimeFormat != 2) {
                currentTimeFormat
            } else if (isSystem24Hour) CLOCK_24H else CLOCK_12H

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

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelAlarm(taskId: Long) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun showDeleteAllDialog(onPositive: (outdatedDeleteOption: OutdatedDeleteOption) -> Unit) {
        val dialogOptions = arrayOf(
            resources.getString(R.string.all_outdated),
            resources.getString(R.string.outdated_completed)
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
                    0 -> onPositive(OutdatedDeleteOption.ALL_OUTDATED)
                    1 -> onPositive(OutdatedDeleteOption.ALL_OUTDATED_COMPLETED)
                }
            }.show()
    }

    private fun showConfirmationDialog(
        onPositive: () -> Unit
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.delete_tasks))
            .setMessage(resources.getString(R.string.tasks_dialog_message))
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                onPositive()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvOutdatedTask.adapter = null
        searchView = null
        _binding = null
    }
}
