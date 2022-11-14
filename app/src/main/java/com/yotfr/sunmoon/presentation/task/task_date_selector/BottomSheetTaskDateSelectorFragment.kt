package com.yotfr.sunmoon.presentation.task.task_date_selector

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentBottomSheetDateTimeSelectorBinding
import com.yotfr.sunmoon.presentation.task.add_task.BottomSheetAddTaskFragment
import com.yotfr.sunmoon.presentation.task.task_date_selector.event.BottomSheetDateSelectorUiEvent
import com.yotfr.sunmoon.presentation.task.task_date_selector.event.BottomSheetTaskDateSelectorEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BottomSheetTaskDateSelectorFragment : BottomSheetDialogFragment() {

    companion object {
        const val WITHOUT_DATE = -1L
        const val WITHOUT_TIME = -1L
    }

    private lateinit var binding: FragmentBottomSheetDateTimeSelectorBinding
    private val viewModel by viewModels<BottomSheetTaskDateSelectorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDateTimeSelectorBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //add constraints to calendarView
        binding.calendarView.minDate = getCurrentDate()
        binding.calendarView.maxDate = getMaxCalendardate()

        binding.calendarView.setOnDateChangeListener { _, year, month, date ->
            binding.btnWithoutDate.visibility = View.GONE
            binding.btnSelectedTime.isEnabled = true
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.set(year, month, date, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            viewModel.onEvent(
                BottomSheetTaskDateSelectorEvent.DateTimeChanged(
                    date = calendar.timeInMillis
                )
            )
        }

        binding.chipDateHelpers.setOnCheckedStateChangeListener { btn, _ ->
            if (btn.checkedChipId == View.NO_ID) {
                //if unselect chip clear date&time
                viewModel.onEvent(
                    BottomSheetTaskDateSelectorEvent.ClearDateTime
                )
            } else {
                parseSelectedChipAndChangeDate(btn.findViewById<Chip>(btn.checkedChipId).tag as String)
            }
        }

        //collect uiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    //update time chip text
                    binding.btnSelectedTime.text = parseTimeToTimeButtonText(
                        currentTimePattern = viewModel.timePattern.value,
                        state.selectedTime
                    )
                    //select chip in chipGroup if one of chips match collected date
                    parseDateAndSelectChip(state.selectedDate)
                    //select date in calendarView that matches collected date
                    state.selectedDate?.let { date ->
                        binding.calendarView.setDate(
                            date,
                            true,
                            false
                        )
                    }
                    //show withoutDate btn if collected date is null
                    changeBtnWithoutDateVisibility(
                        isVisible = state.selectedDate == null
                    )
                }
            }
        }

        //collect uiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is BottomSheetDateSelectorUiEvent.SaveDateTime -> {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                BottomSheetAddTaskFragment.DATE_KEY,
                                uiEvent.date
                            )
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                BottomSheetAddTaskFragment.TIME_KEY,
                                uiEvent.time
                            )
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }

        //save selected date and time and pop to addTask
        binding.btnReschedule.setOnClickListener {
            changeBtnWithoutDateVisibility(true)
            viewModel.onEvent(BottomSheetTaskDateSelectorEvent.SaveDateTimePressed)
        }

        //pop to add task without selected date and time
        binding.btnCancel.setOnClickListener {
            changeBtnWithoutDateVisibility(true)
            findNavController().popBackStack()
        }

        //show time picker
        binding.btnSelectedTime.setOnClickListener {
            showTimePicker(
                currentTimeFormat = viewModel.timeFormat.value
            ) { time ->
                viewModel.onEvent(
                    BottomSheetTaskDateSelectorEvent.DateTimeChanged(
                        time = time
                    )
                )
            }
        }
    }


    private fun showTimePicker(
        currentTimeFormat: Int,
        onPositive: (time: Long) -> Unit
    ) {
        val calendar = Calendar.getInstance(Locale.getDefault())
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

    //parse pressed date chip, change date and show withoutDate btn if needed
    private fun parseSelectedChipAndChangeDate(tag: String) {
        when (tag) {
            "today" -> {
                changeViewModelDate(DateTemplates.TODAY)
                changeBtnWithoutDateVisibility(false)
            }
            "tomorrow" -> {
                changeViewModelDate(DateTemplates.TOMORROW)
                changeBtnWithoutDateVisibility(false)
            }
            "intwodays" -> {
                changeViewModelDate(DateTemplates.IN_TWO_DAYS)
                changeBtnWithoutDateVisibility(false)
            }
            "nextweek" -> {
                changeViewModelDate(DateTemplates.NEXT_WEEK)
                changeBtnWithoutDateVisibility(false)
            }
            "onweekend" -> {
                changeViewModelDate(DateTemplates.ON_WEEKEND)
                changeBtnWithoutDateVisibility(false)
            }
            "withoutdate" -> {
                viewModel.onEvent(BottomSheetTaskDateSelectorEvent.ClearDateTime)
                changeBtnWithoutDateVisibility(true)
            }
        }
    }

    //send new date to viewModel
    private fun changeViewModelDate(dateTemplates: DateTemplates) {
        viewModel.onEvent(
            BottomSheetTaskDateSelectorEvent.DateTimeChanged(
                date = parseDateTemplatesToDate(dateTemplates)
            )
        )
    }

    //hide/show withoutDate btn
    private fun changeBtnWithoutDateVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.btnWithoutDate.visibility = View.VISIBLE
            binding.btnSelectedTime.isEnabled = false
            binding.chipDateHelpers.findViewWithTag<Chip>("withoutdate").isEnabled = false
        } else {
            binding.btnWithoutDate.visibility = View.GONE
            binding.btnSelectedTime.isEnabled = true
            binding.chipDateHelpers.findViewWithTag<Chip>("withoutdate").isEnabled = true
        }
    }

    //format time
    private fun parseTimeToTimeButtonText(
        currentTimePattern: String,
        time: Long?
    ): String {
        Log.d("TEST","dateSelPat $currentTimePattern")
        val timePattern = viewModel.timePattern.value
        val calendar = Calendar.getInstance(Locale.getDefault())
        val sdfTime = SimpleDateFormat(timePattern, Locale.getDefault())
        time?.let {
            calendar.timeInMillis = time
            return sdfTime.format(calendar.time)
        }
        return getString(R.string.without_time)
    }

    //select chip in chipGroup if one of chips match collected date
    private fun parseDateAndSelectChip(date: Long?) {

        if (date == null) {
            viewModel.onEvent(
                BottomSheetTaskDateSelectorEvent.ChangeClearNeeded(
                    isNeeded = true
                )
            )

            val chipWithoutDate = binding.chipDateHelpers.findViewWithTag<Chip>("withoutdate")
            if (chipWithoutDate.id != binding.chipDateHelpers.checkedChipId) {
                chipWithoutDate.performClick()
            }
            return
        }

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
        selectedDate.timeInMillis = date
        selectedDate.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val selectedWeekDay = selectedDate.get(Calendar.DAY_OF_WEEK)
        val selectedWeek = selectedDate.get(Calendar.WEEK_OF_MONTH)

        when (selectedDate.timeInMillis) {
            currentDate.timeInMillis -> {
                binding.chipToday.isChecked = true
                return
            }
            currentDate.timeInMillis + 86400000 -> {
                binding.chipTomorrow.isChecked = true
                return
            }
            currentDate.timeInMillis + 172800000 -> {
                binding.chipInTwoDays.isChecked = true
                return
            }
            currentDate.timeInMillis + 604800000 -> {
                binding.chipNextWeek.isChecked = true
                return
            }
            else -> {
                if ((selectedWeekDay == Calendar.SATURDAY || selectedWeekDay == Calendar.SUNDAY) &&
                    (selectedWeekDay != currentWeekDay) && (selectedWeek == currentWeek)
                ) {
                    binding.chipOnWeekend.isChecked = true
                    return
                } else {

                    viewModel.onEvent(
                        BottomSheetTaskDateSelectorEvent.ChangeClearNeeded(
                            isNeeded = false
                        )
                    )
                    binding.chipDateHelpers.clearCheck()
                }
            }
        }
    }

    //parse dateTemplates to Long date
    private fun parseDateTemplatesToDate(dateTemplates: DateTemplates): Long {

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return when (dateTemplates) {
            DateTemplates.TODAY -> {
                calendar.timeInMillis
            }
            DateTemplates.TOMORROW -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.timeInMillis
            }
            DateTemplates.IN_TWO_DAYS -> {
                calendar.add(Calendar.DAY_OF_MONTH, 2)
                calendar.timeInMillis
            }
            DateTemplates.NEXT_WEEK -> {
                calendar.add(Calendar.DAY_OF_MONTH, 7)
                calendar.timeInMillis
            }
            DateTemplates.ON_WEEKEND -> {
                while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                calendar.timeInMillis
            }
        }
    }

    //get current date
    private fun getCurrentDate(): Long {
        return Calendar.getInstance(Locale.getDefault()).timeInMillis
    }

    //set maxDate for calendarView six months from the current one
    private fun getMaxCalendardate(): Long {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.add(Calendar.MONTH, 6)
        return calendar.timeInMillis
    }

    //enum for chipTemplates
    enum class DateTemplates {
        TODAY,
        TOMORROW,
        IN_TWO_DAYS,
        NEXT_WEEK,
        ON_WEEKEND
    }
}