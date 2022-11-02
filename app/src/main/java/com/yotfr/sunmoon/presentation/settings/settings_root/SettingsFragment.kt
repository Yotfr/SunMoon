package com.yotfr.sunmoon.presentation.settings.settings_root

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentSettingsBinding
import com.yotfr.sunmoon.presentation.MainActivity
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsEvent
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsUiEvent
import com.yotfr.sunmoon.presentation.settings.settings_root.model.DatePattern
import com.yotfr.sunmoon.presentation.settings.settings_root.model.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        binding.btnChangeTheme.setOnClickListener {
            navigateToThemePicker()
        }

        (requireActivity() as MainActivity).setUpActionBar(binding.fragmentSettingsToolbar)

        binding.btnDateFormat.setOnClickListener {
            showDateFormatSelectorDialog(
                viewModel.settingsUiState.value.datePattern
            ) { datePattern ->
                viewModel.onEvent(
                    SettingsEvent.ChangeDateFormat(
                        dateFormat = datePattern.pattern
                    )
                )
            }
        }

        binding.btnTimeFormat.setOnClickListener {
            showTimeFormatSelectorDialog(
                viewModel.settingsUiState.value.timeFormat,
            ) { timePattern, timeFormat ->
                viewModel.onEvent(
                    SettingsEvent.ChangeTimeFormat(
                        timePattern = timePattern,
                        timeFormat = timeFormat
                    )
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is SettingsUiEvent.ShowChangeRestartSnackbar -> {
                            showRestartRequiredSnackbar()
                        }
                    }
                }
            }
        }
    }

    private fun showRestartRequiredSnackbar() {
        Snackbar.make(
            requireView(),
            getString(R.string.restart_required),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun navigateToThemePicker() {
        val direction = SettingsFragmentDirections.actionSettingsFragmentToSettingsThemeFragment()
        findNavController().navigate(direction)
    }


    private fun showDateFormatSelectorDialog(
        currentDatePattern: DatePattern,
        onResult: (datePattern: DatePattern) -> Unit
    ) {
        val dialogOptions = arrayOf(
            parseCurrentTimeToString(DatePattern.YEAR_FIRST),
            parseCurrentTimeToString(DatePattern.MONTH_FIRST),
            parseCurrentTimeToString(DatePattern.DAY_FIRST),
        )
        val checkedItem = dialogOptions.indexOf(
            parseCurrentTimeToString(currentDatePattern)
        )
        var selectedItem = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.select_date_format))
            .setSingleChoiceItems(dialogOptions, checkedItem) { _, which ->
                selectedItem = which
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.save)) { _, _ ->
                when (selectedItem) {
                    0 -> onResult(DatePattern.YEAR_FIRST)
                    1 -> onResult(DatePattern.MONTH_FIRST)
                    2 -> onResult(DatePattern.DAY_FIRST)
                }
            }.show()
    }


    private fun showTimeFormatSelectorDialog(
        currenTimeFormat: TimeFormat,
        onResultPattern: (timePattern: String, timeFormat:Int) -> Unit
    ) {
        val dialogOptions = arrayOf(
            resources.getString(R.string.system_default),
            resources.getString(R.string.am_pm),
            resources.getString(R.string.normal_hour)
        )
        val checkedItem = when (currenTimeFormat) {
            TimeFormat.SYSTEM_DEFAULT -> {
                0
            }
            TimeFormat.AM_PM -> {
                1
            }
            TimeFormat.NORMAL -> {
                2
            }
        }
        var selectedItem = 0
        val sdfPattern24 = "HH:mm"
        val sdfPattern12 = "h:mm a"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.select_date_format))
            .setSingleChoiceItems(dialogOptions, checkedItem) { _, which ->
                selectedItem = which
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.save)) { _, _ ->
                when (selectedItem) {
                    0 -> {
                        val isSystem24Hour =
                            android.text.format.DateFormat.is24HourFormat(requireContext())
                        if (isSystem24Hour)
                            onResultPattern(sdfPattern24, TimeFormat.SYSTEM_DEFAULT.format)
                        else onResultPattern(
                            sdfPattern12, TimeFormat.SYSTEM_DEFAULT.format
                        )
                    }
                    1 -> {
                        onResultPattern(
                            sdfPattern12,
                            TimeFormat.AM_PM.format
                        )
                    }
                    2 -> {
                        onResultPattern(
                            sdfPattern24,
                            TimeFormat.NORMAL.format
                        )
                    }
                }
            }.show()
    }


    private fun parseCurrentTimeToString(format: DatePattern): String {
        val pattern = format.pattern
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance(Locale.getDefault())
        return sdf.format(calendar.time)
    }


}