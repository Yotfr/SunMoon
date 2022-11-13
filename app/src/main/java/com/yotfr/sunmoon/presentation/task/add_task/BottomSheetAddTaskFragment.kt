package com.yotfr.sunmoon.presentation.task.add_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentBottomSheetAddTaskBinding
import com.yotfr.sunmoon.presentation.task.TaskRootFragment
import com.yotfr.sunmoon.presentation.task.add_task.event.BottomSheetAddTaskEvent
import com.yotfr.sunmoon.presentation.task.add_task.event.BottomSheetAddTaskUiEvent
import com.yotfr.sunmoon.presentation.task.task_date_selector.BottomSheetTaskDateSelectorFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BottomSheetAddTaskFragment : BottomSheetDialogFragment() {

    companion object {
        const val DATE_KEY = "DATE_KEY"
        const val TIME_KEY = "TIME_KEY"
        const val WITHOUT_SELECTED_DATE = -1L
        const val WITHOUT_SELECTED_TIME = -1L
    }

    private lateinit var binding: FragmentBottomSheetAddTaskBinding
    private val viewModel by viewModels<BottomSheetAddTaskViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //remove backStack StateFlow values
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long?>(
            DATE_KEY
        )
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long?>(
            TIME_KEY
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetAddTaskBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //focus edit text when open bottomSheet
        binding.textFieldTaskDescription.editText?.isFocusableInTouchMode = true
        binding.textFieldTaskDescription.editText?.requestFocus()

        //enable-disable save btn
        binding.textFieldTaskDescription.editText?.doOnTextChanged { charSequence, _, _, _ ->
            binding.btnAddTask.isEnabled = !charSequence.isNullOrBlank()
        }

        //get date and time from viewModel needed for navigate to dateSelector
        binding.chipScheduledDate.setOnClickListener {
            viewModel.onEvent(BottomSheetAddTaskEvent.NavigateToDateSelector)
        }

        //show time picker and change date chip text
        binding.chipScheduledTime.setOnClickListener {
            showTimePicker(
                currentTimeFormat = viewModel.timeFormat.value,
                currentSelectedTime = viewModel.uiState.value.selectedTime
            ) { time ->
                viewModel.onEvent(
                    BottomSheetAddTaskEvent.ChangeTime(
                        newTime = time
                    )
                )
            }
        }

        //save task
        binding.btnAddTask.setOnClickListener {
            val taskDescription = binding.textFieldTaskDescription.editText?.text.toString()
            viewModel.onEvent(
                BottomSheetAddTaskEvent.AddTask(
                    taskDescription = taskDescription
                )
            )
        }

        //collectUiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.chipScheduledDate.text =
                        parseSelectedDateToChipText(state.selectedDate)
                    binding.chipScheduledTime.text =
                        parseSelectedTimeToChipText(
                            currentTimePattern = viewModel.timePattern.value,
                            state.selectedTime
                        )
                }
            }
        }

        //collect date from dateSelector
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val navController = findNavController()
                navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Long?>(
                    DATE_KEY, null
                )?.collect { date ->
                    val selectedDate = if (date == WITHOUT_SELECTED_DATE) null else date
                    viewModel.onEvent(
                        BottomSheetAddTaskEvent.ChangeDate(
                            newDate = selectedDate
                        )
                    )
                }
            }
        }

        //collect time from dateSelector
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val navController = findNavController()
                navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Long?>(
                    TIME_KEY, null
                )?.collect { time ->
                    val selectedTime = if (time == WITHOUT_SELECTED_TIME) null else time
                    viewModel.onEvent(
                        BottomSheetAddTaskEvent.ChangeTime(
                            newTime = selectedTime
                        )
                    )
                }
            }
        }

        //collect uiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvents.collect { uiEvent ->
                    when (uiEvent) {
                        is BottomSheetAddTaskUiEvent.NavigateToDateSelector -> {
                            val directions = BottomSheetAddTaskFragmentDirections
                                .actionBottomSheetAddTaskFragmentToBottomSheetTaskDateSelectorFragment(
                                    uiEvent.date ?: BottomSheetTaskDateSelectorFragment
                                        .WITHOUT_DATE,
                                    uiEvent.time ?: BottomSheetTaskDateSelectorFragment
                                        .WITHOUT_TIME
                                )
                            navigateToDestination(
                                direction = directions
                            )
                        }
                        is BottomSheetAddTaskUiEvent.PopBackStack -> {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                TaskRootFragment.SELECTED_TASK_DATE,
                                uiEvent.date
                            )
                            navigateToDestination()
                        }
                    }
                }
            }
        }
    }

    private fun showTimePicker(
        currentTimeFormat: Int,
        currentSelectedTime: Long?,
        onPositive: (time: Long) -> Unit
    ) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        currentSelectedTime?.let { time ->
            calendar.timeInMillis = time
        }
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(activity)
        val clockFormat = if (currentTimeFormat != 2) {
            currentTimeFormat
        } else if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
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

    //format Long date
    private fun parseSelectedDateToChipText(selectedDateInMillis: Long?): String {
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        val currentDate = Calendar.getInstance(Locale.getDefault())
        currentDate.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val currentWeekDay = currentDate.get(Calendar.DAY_OF_WEEK)
        val currentWeek = currentDate.get(Calendar.WEEK_OF_MONTH)

        val selectedDate = Calendar.getInstance(Locale.getDefault())
        selectedDateInMillis?.let { date ->
            selectedDate.timeInMillis = date
            val selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)
            val selectedWeek = selectedDate.get(Calendar.WEEK_OF_MONTH)

            if ((selectedDayOfWeek == Calendar.SUNDAY || selectedDayOfWeek == Calendar.SATURDAY) &&
                (selectedDayOfWeek != currentWeekDay) && (currentWeek == selectedWeek)
            ) {
                return getString(R.string.on_weekend)
            }
        }
        return when (selectedDateInMillis) {
            currentDate.timeInMillis -> getString(R.string.today)
            currentDate.timeInMillis + 86400000 -> getString(R.string.tomorrow)
            currentDate.timeInMillis + 172800000 -> getString(R.string.in_two_days)
            currentDate.timeInMillis + 604800000 -> getString(R.string.next_week)
            null -> getString(R.string.without_date)
            else -> sdf.format(selectedDate.time)
        }
    }

    //parse Long time
    private fun parseSelectedTimeToChipText(
        currentTimePattern: String,
        selectedTimeInMillis: Long?
    ): String {
        val sdf = SimpleDateFormat(currentTimePattern, Locale.getDefault())
        val selectedTime = Calendar.getInstance(Locale.getDefault())
        selectedTimeInMillis?.let {
            selectedTime.timeInMillis = selectedTimeInMillis
            return sdf.format(selectedTime.time)
        }
        return getString(R.string.without_time)
    }

    //navigate to dateSelector or popBackStack
    private fun navigateToDestination(
        direction: NavDirections? = null
    ) {
        if (direction == null) {
            findNavController().popBackStack()
        } else {
            findNavController().navigate(direction)
        }
    }
}