package com.yotfr.sunmoon.presentation.task.task_details

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.google.android.material.transition.MaterialContainerTransform
import com.yotfr.sunmoon.AlarmReceiver
import com.yotfr.sunmoon.R
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.yotfr.sunmoon.databinding.FragmentTaskDetailsBinding
import com.yotfr.sunmoon.presentation.MainActivity
import com.yotfr.sunmoon.presentation.task.TaskRootFragment
import com.yotfr.sunmoon.presentation.task.task_details.adapter.SubTaskAdapter
import com.yotfr.sunmoon.presentation.task.task_details.adapter.SubTaskDelegate
import com.yotfr.sunmoon.presentation.task.task_details.adapter.SubTaskListItemCallback
import com.yotfr.sunmoon.presentation.task.task_details.event.TaskDetailsEvent
import com.yotfr.sunmoon.presentation.task.task_details.event.TaskDetailsUiEvent
import com.yotfr.sunmoon.presentation.task.task_details.model.State
import com.yotfr.sunmoon.presentation.task.task_details.model.SubTaskModel
import com.yotfr.sunmoon.presentation.utils.getColorFromAttr
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TaskDetailsFragment : Fragment(R.layout.fragment_task_details) {

    companion object {
        const val FROM_SCHEDULED = 0
        const val FROM_UNPLANNED = 1
        const val FROM_OUTDATED = 2
    }

    private val viewModel by viewModels<TaskDetailsViewModel>()

    private val args: TaskDetailsFragmentArgs by navArgs()

    private lateinit var binding: FragmentTaskDetailsBinding
    private lateinit var subTaskAdapter: SubTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.activity_main_fragment_container
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(
                requireContext().getColorFromAttr(
                    com.google.android.material.R.attr.colorSurface
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskDetailsBinding.bind(view)

        //setUpToolbar
        (requireActivity() as MainActivity).setUpActionBar(binding.fragmentTaskDetailsMaterialToolbar)

        //inflateMenu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.app_bar_menu_task_details, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_delete_task -> {
                        showDeleteTaskDialog {
                            viewModel.onEvent(TaskDetailsEvent.DeleteTask)
                        }
                        true
                    }
                    android.R.id.home -> {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            TaskRootFragment.SELECTED_TASK_DATE,
                            viewModel.uiState.value.scheduledDate
                        )
                        findNavController().popBackStack()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)


        //setTransitionName
        ViewCompat.setTransitionName(binding.root, "task${args.taskId}")

        //initRvAdapters
        val layoutManager = LinearLayoutManager(requireContext())
        subTaskAdapter = SubTaskAdapter()
        binding.fragmentTaskDetailsRecyclerview.adapter = subTaskAdapter
        binding.fragmentTaskDetailsRecyclerview.layoutManager = layoutManager
        subTaskAdapter.attachDelegate(object : SubTaskDelegate {
            override fun subTaskCheckBoxClicked(subTask: SubTaskModel) {
                viewModel.onEvent(
                    TaskDetailsEvent.ChangeSubTaskCompletionStatus(
                        subTask = subTask
                    )
                )
            }

            override fun addSubTaskPressed() {
                viewModel.onEvent(TaskDetailsEvent.GetTaskId)
            }

            override fun subTaskTextChanged(subTask: SubTaskModel, newText: String) {
                viewModel.onEvent(
                    TaskDetailsEvent.ChangeSubTaskText(
                        subTask = subTask,
                        newText = newText
                    )
                )
            }

            override fun removeEmptySubTask(subTask: SubTaskModel) {
                viewModel.onEvent(
                    TaskDetailsEvent.DeleteSubTask(
                        subTask = subTask
                    )
                )
            }
        })
        initSwipeToDelete()

        //Change task text state
        binding.fragmentTaskDetailsTaskDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(
                TaskDetailsEvent.ChangeTaskText(
                    newText = text.toString()
                )
            )
        }

        //apply task text changes and save task
        binding.fragmentTaskDetailsTaskDescription.setOnFocusChangeListener { _, isFocused ->
            if (!isFocused) {
                viewModel.onEvent(TaskDetailsEvent.ApplyTaskTextChange)
            }
        }

        //change task completionStatus
        binding.fragmentTaskDetailMarkUndone.setOnClickListener {
            viewModel.onEvent(TaskDetailsEvent.MarkUndone)
        }

        //clear task date&time
        binding.fragmentTaskDetailMakeUnplanned.setOnClickListener {
            viewModel.onEvent(TaskDetailsEvent.ClearDateTime)
        }

        //get date from viewModel and show datePicker
        binding.fragmentTaskDetailsDateTimeTv.setOnClickListener {
            viewModel.onEvent(TaskDetailsEvent.GetDateForDatePicker)
        }

        //get time from viewModel and show timePicker
        binding.fragmentTaskDetailsDateTimeTime.setOnClickListener {
            viewModel.onEvent(TaskDetailsEvent.GetTimeForTimePicker)
        }

        //get time from viewModel and show timePicker
        binding.fragmentTaskDetailsDateTimeTvSetTime.setOnClickListener {
            viewModel.onEvent(TaskDetailsEvent.GetTimeForTimePicker)
        }

        //clear task date&time
        binding.fragmentTaskDetailsDateTimeDateClearBtn.setOnClickListener {
            viewModel.onEvent(TaskDetailsEvent.ClearDateTime)
        }

        //show dateTimePicker to set reminder
        binding.fragmentTaskDetailsRemindTvSet.setOnClickListener {
            if (checkNotificationPermission()) {
                showReminderDateTimePicker(
                    currentTimeFormat = viewModel.dateTimeSettings.value.timeFormat
                ) { date, time, remindTime ->
                    viewModel.onEvent(
                        TaskDetailsEvent.SetTaskRemindDateTime(
                            remindDate = date,
                            remindTime = time,
                            remindInMillis = remindTime
                        )
                    )
                }
            }
        }

        //show dateTimePicker to set reminder
        binding.fragmentTaskDetailsRemindTvTime.setOnClickListener {
            if (checkNotificationPermission()) {
                showReminderDateTimePicker(
                    currentTimeFormat = viewModel.dateTimeSettings.value.timeFormat
                ) { date, time, remindTime ->
                    viewModel.onEvent(
                        TaskDetailsEvent.ChangeTaskRemindDateTime(
                            remindDate = date,
                            remindTime = time,
                            remindInMillis = remindTime
                        )
                    )
                }
            }
        }

        //show dateTimePicker to set reminder
        binding.fragmentTaskDetailsRemindTvDate.setOnClickListener {
            if (checkNotificationPermission()) {
                showReminderDateTimePicker(
                    currentTimeFormat = viewModel.dateTimeSettings.value.timeFormat
                ) { date, time, remindTime ->
                    viewModel.onEvent(
                        TaskDetailsEvent.ChangeTaskRemindDateTime(
                            remindDate = date,
                            remindTime = time,
                            remindInMillis = remindTime
                        )
                    )
                }
            }
        }

        //clear reminder date&time
        binding.fragmentTaskDetailsRemindClearBtn.setOnClickListener {
            viewModel.onEvent(TaskDetailsEvent.ClearTaskRemindDateTime)
        }

        //clear task time
        binding.fragmentTaskDetailsDateTimeTimeClearBtn.setOnClickListener {
            viewModel.onEvent(
                TaskDetailsEvent.ChangeTaskScheduledTime(
                    time = null
                )
            )
        }

        //show dateTimePicker and change task date&time
        binding.fragmentTaskDetailSchedule.setOnClickListener {
            showDateTimePicker(
                currentTimeFormat = viewModel.dateTimeSettings.value.timeFormat
            ) { date, time ->
                viewModel.onEvent(
                    TaskDetailsEvent.ChangeTaskScheduledDateTime(
                        date = date,
                        time = time
                    )
                )
            }
        }

        //collectState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.apply {
                        fragmentTaskDetailsTaskDescription.setText(state.taskDescription)
                        fragmentTaskDetailsDateTimeTv.text = dateParser(
                            currentDateFormat = viewModel.dateTimeSettings.value.datePattern,
                            state.scheduledDate
                        )
                        fragmentTaskDetailsDateTimeTime.text = timeParser(
                            currentTimePattern = viewModel.dateTimeSettings.value.timePattern,
                            state.scheduledTime
                        )
                        fragmentTaskDetailsRemindTvDate.text = dateParser(
                            currentDateFormat = viewModel.dateTimeSettings.value.datePattern,
                            state.remindDate
                        )
                        fragmentTaskDetailsRemindTvTime.text = timeParser(
                            currentTimePattern = viewModel.dateTimeSettings.value.timePattern,
                            state.remindTime
                        )
                        subTaskAdapter.subTasks = state.subTasks
                        fragmentTaskDetailsLinearProgress.progress = state.completionProgress

                        if (state.scheduledDate == null) {
                            binding.fragmentTaskDetailsDateTimeDateClearBtn.visibility =
                                View.GONE
                            binding.fragmentTaskDetailsDateTimeTime.isEnabled = false
                        } else {
                            binding.fragmentTaskDetailsDateTimeDateClearBtn.visibility =
                                View.VISIBLE
                            binding.fragmentTaskDetailsDateTimeTime.isEnabled = true
                        }

                        if (state.scheduledTime == null) {
                            fragmentTaskDetailsDateTimeTimeClearBtn.visibility = View.GONE
                            fragmentTaskDetailsDateTimeTime.visibility = View.GONE
                            fragmentTaskDetailsDateTimeTvSetTime.visibility = View.VISIBLE
                        } else {
                            fragmentTaskDetailsDateTimeTimeClearBtn.visibility = View.VISIBLE
                            fragmentTaskDetailsDateTimeTime.visibility = View.VISIBLE
                            fragmentTaskDetailsDateTimeTvSetTime.visibility = View.GONE
                        }

                        if (state.remindDate == null) {
                            fragmentTaskDetailsRemindTvSet.visibility = View.VISIBLE
                            fragmentTaskDetailsRemindTvDate.visibility = View.GONE
                            fragmentTaskDetailsRemindTvTime.visibility = View.GONE
                            fragmentTaskDetailsRemindClearBtn.visibility = View.GONE
                        } else {
                            fragmentTaskDetailsRemindTvSet.visibility = View.GONE
                            fragmentTaskDetailsRemindTvDate.visibility = View.VISIBLE
                            fragmentTaskDetailsRemindTvTime.visibility = View.VISIBLE
                            fragmentTaskDetailsRemindClearBtn.visibility = View.VISIBLE
                        }

                        fragmentTaskDetailsDateTimeTimeClearBtn.visibility =
                            if (state.scheduledTime == null) {
                                View.GONE
                            } else View.VISIBLE

                        parseState(state.state, state.completionStatus)
                    }
                }
            }
        }

        //collect uiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvents.collect { uiEvent ->
                    when (uiEvent) {
                        is TaskDetailsUiEvent.PopBackStack -> {
                            findNavController().popBackStack()
                        }
                        is TaskDetailsUiEvent.ShowDatePicker -> {
                            showDatePicker(uiEvent.selectionDate) { date ->
                                viewModel.onEvent(
                                    TaskDetailsEvent.ChangeTaskScheduledDate(
                                        date = date
                                    )
                                )
                            }
                        }
                        is TaskDetailsUiEvent.ShowTimePicker -> {
                            showTimePicker(
                                currentTimeFormat = viewModel.dateTimeSettings.value.timeFormat
                            ) { time ->
                                viewModel.onEvent(
                                    TaskDetailsEvent.ChangeTaskScheduledTime(
                                        time = time
                                    )
                                )
                            }
                        }
                        is TaskDetailsUiEvent.ShowUndoDeleteSubTask -> {
                            showUndoDeleteSubTaskSnackbar {
                                viewModel.onEvent(
                                    TaskDetailsEvent.UndoDeleteSubTask(
                                        subTask = uiEvent.subTask
                                    )
                                )
                            }
                        }
                        is TaskDetailsUiEvent.NavigateToAddSubTask -> {
                            navigateToAddSubTaskDialog(
                                taskId = uiEvent.taskId
                            )
                        }
                        is TaskDetailsUiEvent.ClearAlarm -> {
                            cancelAlarm(taskId = uiEvent.taskId)
                        }
                        is TaskDetailsUiEvent.SetAlarm -> {
                            if (!uiEvent.isNewAlarm) {
                                cancelAlarm(taskId = uiEvent.taskId)
                            }
                            setAlarm(
                                taskId = uiEvent.taskId,
                                taskTitle = uiEvent.taskDescription,
                                remindTime = uiEvent.alarmTime,
                                destination = uiEvent.destination
                            )
                        }
                    }
                }
            }
        }
    }

    //check if user have notification permission and request it if not
    private fun checkNotificationPermission(): Boolean {
        var hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED)
        } else true
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasNotificationPermission = isGranted
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        return hasNotificationPermission
    }

    //set task reminder with alarmManager
    private fun setAlarm(
        taskTitle: String, taskId: Long, remindTime: Long,
        destination: Int
    ) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("taskTitle", taskTitle)
        intent.putExtra("taskId", taskId)
        intent.putExtra("destination", destination)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            taskId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, remindTime, pendingIntent)
    }

    //cancel task reminder
    private fun cancelAlarm(taskId: Long) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            taskId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }


    private fun showDeleteTaskDialog(onPositive: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm_delete))
            .setMessage(resources.getString(R.string.sure_delete))
            .setNegativeButton(resources.getString(R.string.NO)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                onPositive()
            }.show()
    }

    //initialize itemTouchCallback
    private fun initSwipeToDelete() {
        val onSubTaskItemTrashed = { positionToRemove: Int ->
            val subTask = subTaskAdapter.subTasks[positionToRemove]
            viewModel.onEvent(
                TaskDetailsEvent.DeleteSubTask(
                    subTask = subTask
                )
            )
        }
        val subTaskListItemCallback = SubTaskListItemCallback(
            onItemTrashed = onSubTaskItemTrashed
        )
        ItemTouchHelper(subTaskListItemCallback).attachToRecyclerView(binding.fragmentTaskDetailsRecyclerview)
    }

    private fun showUndoDeleteSubTaskSnackbar(onAction: () -> Unit) {
        Snackbar.make(
            requireView(),
            getString(R.string.undo_delete_subtask_description),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo_delete_task_button_text)) {
                onAction()
            }.show()
    }

    private fun showTimePicker(
        currentTimeFormat: Int,
        onPositive: (time: Long) -> Unit
    ) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())

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
            calendar.set(0, 0, 0, picker.hour, picker.minute)
            calendar.set(Calendar.MILLISECOND, 0)
            onPositive(calendar.timeInMillis)
        }
    }

    private fun showDatePicker(selectionDate: Long? = null, onPositive: (date: Long) -> Unit) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(
                selectionDate
                    ?: MaterialDatePicker.todayInUtcMilliseconds()
            )
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        datePicker.show(parentFragmentManager, "tag")
        datePicker.addOnPositiveButtonClickListener { date ->
            calendar.timeInMillis = date
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onPositive(calendar.timeInMillis)
        }
    }

    private fun showReminderDateTimePicker(
        currentTimeFormat: Int,
        onResult: (date: Long, time: Long, remindTime: Long) -> Unit
    ) {
        val calendarDate = Calendar.getInstance(Locale.getDefault())
        var selectedDate: Long
        var selectedTime: Long
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
            val isSystem24Hour = DateFormat.is24HourFormat(requireContext())

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

                calendarDate.set(Calendar.HOUR_OF_DAY, picker.hour)
                calendarDate.set(Calendar.MINUTE, picker.minute)
                val remindTime =
                    calendarDate.timeInMillis

                calendarTime.set(0, 0, 0, picker.hour, picker.minute)
                calendarTime.set(Calendar.MILLISECOND, 0)
                selectedTime = calendarTime.timeInMillis
                onResult(selectedDate, selectedTime, remindTime)
            }
        }
    }

    private fun showDateTimePicker(
        currentTimeFormat: Int,
        onResult: (date: Long, time: Long?) -> Unit
    ) {
        val calendarDate = Calendar.getInstance(Locale.getDefault())
        var selectedDate: Long
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
            val isSystem24Hour = DateFormat.is24HourFormat(requireContext())

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
                onResult(selectedDate, selectedTime)
            }
            picker.addOnNegativeButtonClickListener {
                onResult(selectedDate, selectedTime)
            }
            picker.addOnCancelListener {
                onResult(selectedDate, selectedTime)
            }
            picker.addOnDismissListener {
                onResult(selectedDate, selectedTime)
            }
        }
    }

    private fun navigateToAddSubTaskDialog(taskId: Long) {
        val direction =
            TaskDetailsFragmentDirections.actionTaskDetailsFragmentToAddSubTaskDialogFragment(
                taskId = taskId
            )
        findNavController().navigate(direction)
    }

    //get state and update ui
    private fun parseState(state: State, completionStatus: Boolean) {
        when (state) {
            State.SCHEDULED -> {
                if (completionStatus) {
                    makeUndone()
                } else {
                    makeScheculed()
                }
            }
            State.UNPLANNED -> {
                if (completionStatus) {
                    makeUndone()
                } else {
                    makeUnplanned()
                }
            }
            State.OUTDATED -> {
                if (completionStatus) {
                    makeUndone()
                } else {
                    makeOutdated()
                }
            }
        }
    }

    //format date
    private fun dateParser(
        currentDateFormat: String,
        date: Long?
    ): String {
        var returnDate = getString(R.string.without_date)

        val calendar = Calendar.getInstance(Locale.getDefault())
        val sdfDate = SimpleDateFormat(currentDateFormat)
        date?.let {
            calendar.timeInMillis = it
            returnDate = sdfDate.format(calendar.time)
        }

        return returnDate
    }

    //format time
    private fun timeParser(
        currentTimePattern: String,
        time: Long?
    ): String? {
        if (time == null) return null
        val calendar = Calendar.getInstance(Locale.getDefault())
        val sdfDate = SimpleDateFormat(currentTimePattern, Locale.getDefault())
        calendar.timeInMillis = time
        return sdfDate.format(calendar.time)
    }

    //update ui to make task scheduled
    private fun makeScheculed() {
        binding.apply {
            fragmentTaskDetailsSubtaskCard.alpha = 1f
            fragmentTaskDetailsTaskDescription.isEnabled = true
            fragmentTaskDetailsRecyclerview.isEnabled = true
            fragmentTaskDetailsDateTimeCard.showAllChildren()
            fragmentTaskDetailsRemindCard.showAllChildren()
            fragmentTaskDetailSchedule.hide()
            fragmentTaskDetailMakeUnplanned.hide()
            fragmentTaskDetailMarkUndone.hide()
        }
    }

    //update ui to make task unplanned
    private fun makeUnplanned() {
        binding.apply {
            fragmentTaskDetailsSubtaskCard.alpha = 1f
            fragmentTaskDetailsTaskDescription.isEnabled = true
            fragmentTaskDetailsRecyclerview.isEnabled = true
            fragmentTaskDetailsDateTimeCard.hideAllChildren()
            fragmentTaskDetailsRemindCard.hideAllChildren()
            fragmentTaskDetailSchedule.show()
            fragmentTaskDetailMakeUnplanned.hide()
            fragmentTaskDetailMarkUndone.hide()
        }
    }

    //update ui to make task outdated
    private fun makeOutdated() {
        binding.apply {
            fragmentTaskDetailsSubtaskCard.alpha = 0.5f
            fragmentTaskDetailsTaskDescription.isEnabled = false
            fragmentTaskDetailsRecyclerview.isEnabled = false
            fragmentTaskDetailsDateTimeCard.hideAllChildren()
            fragmentTaskDetailsRemindCard.hideAllChildren()
            fragmentTaskDetailSchedule.show()
            fragmentTaskDetailMakeUnplanned.show()
            fragmentTaskDetailMarkUndone.hide()
        }
    }

    //update ui to make task uncompleted
    private fun makeUndone() {
        binding.apply {
            fragmentTaskDetailsSubtaskCard.alpha = 0.5f
            fragmentTaskDetailsTaskDescription.isEnabled = false
            fragmentTaskDetailsRecyclerview.isEnabled = false
            fragmentTaskDetailsDateTimeCard.hideAllChildren()
            fragmentTaskDetailsRemindCard.hideAllChildren()
            fragmentTaskDetailSchedule.hide()
            fragmentTaskDetailMakeUnplanned.hide()
            fragmentTaskDetailMarkUndone.show()
        }
    }

    private fun View.hide() {
        visibility = View.GONE
    }

    private fun View.show() {
        visibility = View.VISIBLE
    }

    private fun ViewGroup.hideAllChildren() {
        this.forEach {
            it.hide()
        }
    }

    private fun ViewGroup.showAllChildren() {
        this.forEach {
            it.show()
        }
    }
}