package com.yotfr.sunmoon.presentation.task.unplanned_task_list

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
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
import com.yotfr.sunmoon.databinding.FragmentUnplannedTaskListBinding
import com.yotfr.sunmoon.presentation.task.TaskRootFragment
import com.yotfr.sunmoon.presentation.utils.onQueryTextChanged
import com.yotfr.sunmoon.presentation.task.TaskRootFragmentDirections
import com.yotfr.sunmoon.presentation.task.task_details.TaskDetailsFragment
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.adapter.*
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.event.UnplannedTaskListEvent
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.event.UnplannedTaskListUiEvent
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedDeleteOption
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListModel
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import com.yotfr.sunmoon.presentation.utils.getColorFromAttr
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class UnplannedTaskListFragment : Fragment(R.layout.fragment_unplanned_task_list) {

    private val viewModel by viewModels<UnplannedTaskListViewModel>()

    private var searchView: SearchView? = null

    private var _binding: FragmentUnplannedTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var unplannedUncompletedTaskListAdapter: UnplannedUncompletedTaskListAdapter
    private lateinit var unplannedCompletedTaskListAdapter: UnplannedCompletedTaskListAdapter
    private lateinit var unplannedCompletedTaskHeaderAdapter: UnplannedCompletedTaskHeaderAdapter
    private lateinit var unplannedFooterAdapter: UnplannedFooterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUnplannedTaskListBinding.bind(view)

        //inflateMenu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
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
                        UnplannedTaskListEvent.UpdateSearchQuery(
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
                                viewModel.onEvent(UnplannedTaskListEvent.DeleteTasks(
                                    deleteOption = deleteOption
                                ))
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        //initAdapters
        val layoutManager = LinearLayoutManager(requireContext())
        unplannedUncompletedTaskListAdapter = UnplannedUncompletedTaskListAdapter()
        unplannedUncompletedTaskListAdapter.attachDelegate(object :
            UnplannedUncompletedTaskDelegate {
            override fun taskPressed(taskId: Long, transitionView: View) {
                val direction =
                    TaskRootFragmentDirections.actionTaskRootFragmentToTaskDetailsFragment(
                        taskId = taskId,
                        destination = TaskDetailsFragment.FROM_UNPLANNED
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

            override fun taskCheckBoxPressed(task: UnplannedTaskListModel) {
                viewModel.onEvent(
                    UnplannedTaskListEvent.ChangeUnplannedTaskCompletionStatus(
                        task = task
                    )
                )
            }

            override fun scheduleTaskPressed(task: UnplannedTaskListModel) {
                showDateTimePicker(
                    currentTimeFormat = viewModel.timeFormat.value
                ) { selectedDate, selectedTime ->
                    viewModel.onEvent(
                        UnplannedTaskListEvent.ScheduleTask(
                            task = task,
                            selectedDate = selectedDate,
                            selectedTime = selectedTime
                        )
                    )
                }
            }

            override fun taskStarPressed(task: UnplannedTaskListModel) {
                viewModel.onEvent(
                    UnplannedTaskListEvent.ChangeUnplannedTaskImportance(
                        task = task
                    )
                )
            }
        })

        unplannedCompletedTaskListAdapter = UnplannedCompletedTaskListAdapter()
        unplannedCompletedTaskListAdapter.attachDelegate(object : UnplannedCompletedTaskDelegate {
            override fun taskPressed(taskId: Long, transitionView: View) {
                val direction =
                    TaskRootFragmentDirections.actionTaskRootFragmentToTaskDetailsFragment(
                        taskId = taskId,
                        destination = TaskDetailsFragment.FROM_UNPLANNED
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

            override fun taskCheckBoxPressed(task: UnplannedTaskListModel) {
                viewModel.onEvent(
                    UnplannedTaskListEvent.ChangeUnplannedTaskCompletionStatus(
                        task = task
                    )
                )
            }
        })

        unplannedCompletedTaskHeaderAdapter = UnplannedCompletedTaskHeaderAdapter()
        unplannedCompletedTaskHeaderAdapter.attachDelegate(object :
            UnplannedCompletedHeaderDelegate {
            override fun hideCompleted() {
                viewModel.onEvent(UnplannedTaskListEvent.ChangeCompletedTasksVisibility)
            }
        })

        unplannedFooterAdapter = UnplannedFooterAdapter()

        val concatAdapter =
            ConcatAdapter(
                ConcatAdapter.Config.Builder()
                    .setIsolateViewTypes(false)
                    .build(),
                unplannedUncompletedTaskListAdapter,
                unplannedCompletedTaskHeaderAdapter,
                unplannedCompletedTaskListAdapter,
                unplannedFooterAdapter
            )

        binding.fragmentUnplannedTaskRvTaskList.layoutManager = layoutManager
        binding.fragmentUnplannedTaskRvTaskList.adapter = concatAdapter

        binding.fragmentUnplannedTaskRvTaskList.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )
        initSwipeToDelete()

        //CollectUiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state?.let {
                        unplannedUncompletedTaskListAdapter.submitList(it.uncompletedTasks)
                        unplannedCompletedTaskListAdapter.submitList(it.completedTasks)
                        unplannedCompletedTaskHeaderAdapter.headerState = it.headerState
                        unplannedFooterAdapter.footerState = it.footerState
                    }
                }
            }
        }


        //CollectUiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is UnplannedTaskListUiEvent.UndoTrashUnplannedTask -> {
                            showUndoTrashSnackbar(
                                onAction = {
                                    viewModel.onEvent(UnplannedTaskListEvent.UndoTrashItem(
                                        task = uiEvent.task
                                    ))
                                },
                                onDismiss = {
                                    cancelAlarm(uiEvent.task.taskId)
                                }
                            )
                        }
                        is UnplannedTaskListUiEvent.NavigateToScheduledTask -> {
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

    //initialize itemCallback
    private fun initSwipeToDelete() {
        val onUncompletedItemTrashed = { positionToRemove: Int ->
            val task = unplannedUncompletedTaskListAdapter.currentList[positionToRemove]
            viewModel.onEvent(
                UnplannedTaskListEvent.TrashUnplannedTask(
                    task = task
                )
            )
        }
        val onCompletedItemTrashed = { positionToRemove: Int ->
            val task = unplannedCompletedTaskListAdapter.currentList[positionToRemove]
            viewModel.onEvent(
                UnplannedTaskListEvent.TrashUnplannedTask(
                    task = task
                )
            )
        }
        val unplannedTaskListItemCallback = UnplannedTaskListItemCallback(
            onUncompletedItemTrashed = onUncompletedItemTrashed,
            onCompletedItemTrashed = onCompletedItemTrashed
        )
        ItemTouchHelper(unplannedTaskListItemCallback).attachToRecyclerView(binding.fragmentUnplannedTaskRvTaskList)
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

    private fun showDateTimePicker(
        currentTimeFormat:Int,
        onResult: (
            selectedDate: Long, selectedTime: Long?
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
            val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(activity)

            val timeFormat = if (currentTimeFormat != 2) {
                currentTimeFormat
            }else if (isSystem24Hour) CLOCK_24H else CLOCK_12H

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

    private fun showDeleteAllDialog(onPositive: (deleteOption: UnplannedDeleteOption) -> Unit) {
        val dialogOptions = arrayOf(
            resources.getString(R.string.all_unplanned),
            resources.getString(R.string.unplanned_completed)
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
                when(selectedItem) {
                    0 -> {
                        onPositive(UnplannedDeleteOption.ALL_UNPLANNED)
                    }
                    1-> {
                        onPositive(UnplannedDeleteOption.ALL_UNPLANNED_COMPLETED)
                    }
                }
            }
            .show()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelAlarm(taskId: Long) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            taskId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun showConfirmationDialog(
        onPositive:() -> Unit
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.fragmentUnplannedTaskRvTaskList.adapter = null
        searchView = null
        _binding = null
    }



}