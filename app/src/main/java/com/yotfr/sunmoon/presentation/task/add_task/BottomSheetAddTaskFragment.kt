package com.yotfr.sunmoon.presentation.task.add_task

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.MaterialContainerTransform
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentBottomSheetAddTaskBinding
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
        const val WITHOUT_SELECTED_DATE = -1L
        const val WITHOUT_SELECTED_TIME = -1L
    }

    private lateinit var binding: FragmentBottomSheetAddTaskBinding
    private val viewModel by viewModels<BottomSheetAddTaskViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition =  MaterialContainerTransform()
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

        //enable-disable save btn
        binding.textFieldTaskDescription.editText?.doOnTextChanged { charSequence, _, _, _ ->
            binding.btnAddTask.isEnabled = !charSequence.isNullOrBlank()
        }

        //navigate to dateSelector
        binding.chipScheduledDate.setOnClickListener {
            viewModel.onEvent(BottomSheetAddTaskEvent.NavigateToDateSelector)
        }


        //changeScheduledTime
        binding.chipScheduledTime.setOnClickListener {
            showTimePicker(
                currentTimeFormat = viewModel.timeFormat.value
            ) { time ->
                viewModel.onEvent(
                    BottomSheetAddTaskEvent.ChangeTime(
                        newTime = time
                    )
                )
            }

        }

        //addTask
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

        parentFragmentManager.setFragmentResultListener(
            BottomSheetTaskDateSelectorFragment.REQUEST_CODE,
            viewLifecycleOwner
        ) { _, data ->
            val date =
                if (data.getLong(BottomSheetTaskDateSelectorFragment.SELECTED_DATE) == WITHOUT_SELECTED_DATE) {
                    null
                } else data.getLong(BottomSheetTaskDateSelectorFragment.SELECTED_DATE)
            val time =
                if (data.getLong(BottomSheetTaskDateSelectorFragment.SELECTED_TIME) == WITHOUT_SELECTED_TIME) {
                    null
                } else data.getLong(BottomSheetTaskDateSelectorFragment.SELECTED_TIME)
            if (date == null && time == null) {
                viewModel.onEvent(BottomSheetAddTaskEvent.ClearDateTime)
            } else {
                viewModel.onEvent(
                    BottomSheetAddTaskEvent.DateTimeChanged(
                        newDate = date,
                        newTime = time
                    )
                )
            }
        }




        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvents.collect { event ->
                    when (event) {
                        is BottomSheetAddTaskUiEvent.NavigateToDateSelector -> {
                            val directions = BottomSheetAddTaskFragmentDirections
                                .actionBottomSheetAddTaskFragmentToBottomSheetTaskDateSelectorFragment(
                                    event.date ?: BottomSheetTaskDateSelectorFragment
                                        .WITHOUT_DATE,
                                    event.time ?: BottomSheetTaskDateSelectorFragment
                                        .WITHOUT_TIME
                                )
                            findNavController().navigate(directions)
                        }
                        is BottomSheetAddTaskUiEvent.PopBackStack -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }


    private fun showTimePicker(
        currentTimeFormat:Int,
        onPositive: (time: Long) -> Unit
    ) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(activity)
        val clockFormat = if (currentTimeFormat != 2) {
            currentTimeFormat
        }else if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

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
            null -> getString(R.string.set)
            else -> sdf.format(selectedDate.time)
        }
    }

    private fun parseSelectedTimeToChipText(
        currentTimePattern:String,
        selectedTimeInMillis: Long?
    ): String {
        val sdf = SimpleDateFormat(currentTimePattern, Locale.getDefault())
        val selectedTime = Calendar.getInstance(Locale.getDefault())
        selectedTimeInMillis?.let {
            selectedTime.timeInMillis = selectedTimeInMillis
            return sdf.format(selectedTime.time)
        }
        return getString(R.string.set)
    }


}