package com.yotfr.sunmoon.presentation.task.scheduled_task_list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_12H
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentScheduledTaskListBinding
import com.yotfr.sunmoon.presentation.utils.onQueryTextChanged
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter.TaskListItemCallback
import com.yotfr.sunmoon.presentation.task.TaskRootFragmentDirections
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter.*
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.event.ScheduledTaskListEvent
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.event.ScheduledTaskListUiEvent
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskDeleteOption
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel
import com.yotfr.sunmoon.presentation.task.task_details.TaskDetailsFragment
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ScheduledTaskListFragment : Fragment(R.layout.fragment_scheduled_task_list) {

    private val viewModel by viewModels<ScheduledTaskListViewModel>()

    private var searchView: SearchView? = null

    //TODO:viewBinding by delegate
    private lateinit var binding: FragmentScheduledTaskListBinding
    private lateinit var uncompletedTaskListAdapter: ScheduledUncompletedTaskListAdapter
    private lateinit var completedTaskListAdapter: ScheduledCompletedTaskAdapter
    private lateinit var completedTaskHeaderAdapter: ScheduledCompletedHeaderAdapter
    private lateinit var scheduledFooterAdapter: ScheduledFooterAdapter

    //Calendar
    private val lastDayInCalendar = Calendar.getInstance(Locale.getDefault())
    private val sdf = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
    private val cal = Calendar.getInstance(Locale.getDefault())
    private val clickCalendar = Calendar.getInstance(Locale.getDefault())
    private val currentDate = Calendar.getInstance(Locale.getDefault())
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]
    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear
    private val dates = ArrayList<Date>()
    //Calendar


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScheduledTaskListBinding.bind(view)


        //Calendar
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.scheduledTaskListFragmentRvCalendar)
        lastDayInCalendar.add(Calendar.MONTH, 6)
        setUpCalendar()

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
                    searchView?.setQuery(pendingQuery, false)
                }

                searchView?.onQueryTextChanged {
                    viewModel.onEvent(
                        ScheduledTaskListEvent.UpdateSearchQuery(
                            searchQuery = it
                        )
                    )
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_delete_all_tasks -> {
                        showDeleteAllDialog { deleteOption ->
                            viewModel.onEvent(
                                ScheduledTaskListEvent.DeleteTasks(
                                    deleteOption = deleteOption
                                )
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        //ChangeCalendarMonth
        binding.scheduledTaskListFragmentBtnPreviousMonth.setOnClickListener {
            changeCalendarMonth(CalendarChangeDirection.PREVIOUS)
        }

        //ChangeCalendarMonth
        binding.scheduledTaskListFragmentBtnNextMonth.setOnClickListener {
            changeCalendarMonth(CalendarChangeDirection.NEXT)
        }

        //ScrollCalendarToSelectedDate
        binding.scheduledTaskListFragmentTvCurrentDate.setOnClickListener {
            showDatePicker { date ->
                val monthCalendar = Calendar.getInstance(Locale.getDefault())
                monthCalendar.timeInMillis = date
                if (monthCalendar.get(Calendar.MONTH) != cal.get(Calendar.MONTH)) {
                    if (monthCalendar.timeInMillis > cal.timeInMillis) {
                        do {
                            changeCalendarMonth(CalendarChangeDirection.NEXT)
                        } while (
                            cal.get(Calendar.MONTH) != monthCalendar.get(Calendar.MONTH)
                        )
                    } else {
                        do {
                            changeCalendarMonth(CalendarChangeDirection.PREVIOUS)
                        } while (
                            cal.get(Calendar.MONTH) != monthCalendar.get(Calendar.MONTH)
                        )
                    }
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    val position = async {
                        withContext(Dispatchers.Default) {
                            dates.indexOfFirst { it.time in date..date + 86400000 }
                        }
                    }
                    binding.scheduledTaskListFragmentRvCalendar.smoothScrollToPosition(position.await())
                    delay(400)
                    binding.scheduledTaskListFragmentRvCalendar.findViewHolderForAdapterPosition(
                        position.await()
                    )?.itemView?.findViewById<View>(R.id.item_calendar_card)?.performClick()
                }
            }
        }


        //InitRvAdapters
        val layoutManager = LinearLayoutManager(requireContext())
        uncompletedTaskListAdapter = ScheduledUncompletedTaskListAdapter()
        uncompletedTaskListAdapter.attachDelegate(object : ScheduledUncompletedTaskListDelegate {
            override fun taskPressed(taskId: Long) {
                val direction =
                    TaskRootFragmentDirections.actionTaskRootFragmentToTaskDetailsFragment(
                        taskId = taskId,
                        destination = TaskDetailsFragment.FROM_SCHEDULED
                    )
                navigateToDestination(direction = direction)
            }

            override fun taskCheckBoxPressed(task: ScheduledTaskListModel) {
                viewModel.onEvent(
                    ScheduledTaskListEvent.ChangeTaskCompletionStatus(
                        task = task
                    )
                )
            }

            override fun taskTimePressed(task: ScheduledTaskListModel) {
                showTimePicker(
                    timeFormat = viewModel.timeFormat.value
                ) { selectedTime ->
                    viewModel.onEvent(
                        ScheduledTaskListEvent.ChangeTaskScheduledTime(
                            task = task,
                            newTime = selectedTime
                        )
                    )
                }
            }

            override fun taskStarPressed(task: ScheduledTaskListModel) {
                viewModel.onEvent(
                    ScheduledTaskListEvent.ChangeScheduledTaskImportance(
                        task = task
                    )
                )
            }
        })

        completedTaskListAdapter = ScheduledCompletedTaskAdapter()
        completedTaskListAdapter.attachDelegate(object : ScheduledCompletedTaskListDelegate {
            override fun taskPressed(taskId: Long) {
                val direction =
                    TaskRootFragmentDirections.actionTaskRootFragmentToTaskDetailsFragment(
                        taskId = taskId,
                        destination = TaskDetailsFragment.FROM_SCHEDULED
                    )
                //TODO:Rename destination
                navigateToDestination(direction = direction)
            }

            override fun taskCheckBoxPressed(task: ScheduledTaskListModel) {
                viewModel.onEvent(
                    ScheduledTaskListEvent.ChangeTaskCompletionStatus(
                        task = task
                    )
                )
            }
        })


        completedTaskHeaderAdapter = ScheduledCompletedHeaderAdapter()
        completedTaskHeaderAdapter.attachDelegate(object : ScheduledCompletedHeaderDelegate {
            override fun hideCompleted() {
                viewModel.onEvent(ScheduledTaskListEvent.ChangeCompletedTasksVisibility)
            }
        })
        scheduledFooterAdapter = ScheduledFooterAdapter()

        val concatAdapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            uncompletedTaskListAdapter,
            completedTaskHeaderAdapter,
            completedTaskListAdapter,
            scheduledFooterAdapter
            )


        binding.scheduledTaskListFragmentRvTasks.adapter = concatAdapter
        binding.scheduledTaskListFragmentRvTasks.layoutManager = layoutManager

        binding.scheduledTaskListFragmentRvTasks.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )

        initSwipeToDelete()


        //StateCollecting
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state?.let { uiModel ->
                        completedTaskListAdapter.tasks = uiModel.completedTasks
                        uncompletedTaskListAdapter.tasks = uiModel.uncompletedTasks
                        completedTaskHeaderAdapter.headerState = uiModel.headerState
                        scheduledFooterAdapter.footerState = uiModel.footerState
                    }
                }
            }
        }

        //UiEventsCollecting
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is ScheduledTaskListUiEvent.UndoDeleteScheduledTask -> {
                            showUndoDeleteSnackBar {
                                viewModel.onEvent(
                                    ScheduledTaskListEvent.UndoTrashItem(
                                        uiEvent.task
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }


    }

    fun getCurrentSelectedDate(): Long {
        return viewModel.selectedCalendarDate.value
    }


    private fun setUpCalendar(changeMonth: Calendar? = null) {
        binding.scheduledTaskListFragmentTvCurrentDate.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        selectedDay =
            when {
                changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
                else -> currentDay
            }
        selectedMonth =
            when {
                changeMonth != null -> changeMonth[Calendar.MONTH]
                else -> currentMonth
            }
        selectedYear =
            when {
                changeMonth != null -> changeMonth[Calendar.YEAR]
                else -> currentYear
            }
        var currentPosition = 0
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            if (monthCalendar[Calendar.DAY_OF_MONTH] == selectedDay)
                currentPosition = dates.size
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val layoutCalendarManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.scheduledTaskListFragmentRvCalendar.layoutManager = layoutCalendarManager
        binding.scheduledTaskListFragmentRvCalendar.isNestedScrollingEnabled = false
        val calendarAdapter =
            CalendarAdapter(dates, currentDate, changeMonth)
        binding.scheduledTaskListFragmentRvCalendar.adapter = calendarAdapter
        when {
            currentPosition > 2 -> binding.scheduledTaskListFragmentRvCalendar.scrollToPosition(
                currentPosition - 3
            )
            maxDaysInMonth - currentPosition < 2 -> binding.scheduledTaskListFragmentRvCalendar.scrollToPosition(
                currentPosition
            )
            else -> binding.scheduledTaskListFragmentRvCalendar.scrollToPosition(currentPosition)
        }
        calendarAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                clickCalendar.time = dates[position]
                clickCalendar.set(Calendar.HOUR_OF_DAY, 0)
                clickCalendar.set(Calendar.MINUTE, 0)
                clickCalendar.set(Calendar.SECOND, 0)
                clickCalendar.set(Calendar.MILLISECOND, 0)
                viewModel.onEvent(ScheduledTaskListEvent.ChangeSelectedDate(clickCalendar.timeInMillis))
            }
        })
    }


    private fun changeCalendarMonth(direction: CalendarChangeDirection) {
        when (direction) {
            CalendarChangeDirection.PREVIOUS -> {
                if (cal.after(currentDate)) {
                    cal.add(Calendar.MONTH, -1)
                    if (cal == currentDate)
                        setUpCalendar()
                    else
                        setUpCalendar(changeMonth = cal)
                }
            }
            CalendarChangeDirection.NEXT -> {
                if (cal.before(lastDayInCalendar)) {
                    cal.add(Calendar.MONTH, 1)
                    setUpCalendar(changeMonth = cal)
                }
            }
        }
    }


    private fun showDatePicker(onPositive: (date: Long) -> Unit) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        datePicker.show(parentFragmentManager, "tag")
        datePicker.addOnPositiveButtonClickListener { date ->
            calendar.apply {
                timeInMillis = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onPositive(calendar.timeInMillis)
        }
    }


    private fun initSwipeToDelete() {
        val onUncompletedItemTrashed = { positionToRemove: Int ->
            val task = uncompletedTaskListAdapter.tasks[positionToRemove]
            viewModel.onEvent(
                ScheduledTaskListEvent.TrashScheduledTask(
                    task = task
                )
            )
        }
        val onCompletedItemTrashed = { positionToRemove: Int ->
            val task = completedTaskListAdapter.tasks[positionToRemove]
            viewModel.onEvent(
                ScheduledTaskListEvent.TrashScheduledTask(
                    task = task
                )
            )
        }
        val taskListItemCallback = TaskListItemCallback(
            onUncompletedItemTrashed = onUncompletedItemTrashed,
            onCompletedItemTrashed = onCompletedItemTrashed
        )
        ItemTouchHelper(taskListItemCallback).attachToRecyclerView(binding.scheduledTaskListFragmentRvTasks)
    }

    private fun showUndoDeleteSnackBar(onAction: () -> Unit) {
        Snackbar.make(
            requireView(),
            getString(R.string.undo_delete_task_description),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo_delete_task_button_text)) {
                onAction()
            }.show()
    }

    private fun showTimePicker(
        timeFormat:Int,
        onPositive: (date: Long) -> Unit
    ) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(requireContext())
        val format = if (timeFormat != 2) {
            timeFormat
        }else if (isSystem24Hour) CLOCK_24H else CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(format)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTitleText(getString(R.string.select_time))
            .build()

        picker.show(parentFragmentManager, "tag")
        picker.addOnPositiveButtonClickListener {
            calendar.set(0, 0, 0, picker.hour, picker.minute)
            calendar.set(Calendar.MILLISECOND, 0)
            onPositive(calendar.timeInMillis)
        }
    }

    private fun navigateToDestination(direction: NavDirections?) {
        if (direction == null) {
            findNavController().popBackStack()
        } else {
            findNavController().navigate(direction)
        }
    }

    private fun showDeleteAllDialog(onPositive: (scheduledTaskDeleteOption: ScheduledTaskDeleteOption) -> Unit) {
        val dialogOptions = arrayOf(
            resources.getString(R.string.deleteAllScheduled),
            resources.getString(R.string.all_scheduled_completed),
            resources.getString(R.string.deleteSelectedDay),
            resources.getString(R.string.scheduled_completed_selcted)
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
                    0 -> onPositive(ScheduledTaskDeleteOption.ALL_SCHEDULED)
                    1 -> onPositive(ScheduledTaskDeleteOption.ALL_COMPLETED_SCHEDULED)
                    2 -> onPositive(ScheduledTaskDeleteOption.ALL_SCHEDULED_FOR_SELECTED_DAY)
                    3 -> onPositive(ScheduledTaskDeleteOption.ALL_SCHEDULED_COMPLETED_FOR_SELECTED_DAY)
                }
            }.show()
    }

    enum class CalendarChangeDirection {
        NEXT, PREVIOUS
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView = null
    }
}