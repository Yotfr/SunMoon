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
import com.google.android.material.timepicker.TimeFormat.*
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentSettingsBinding
import com.yotfr.sunmoon.presentation.MainActivity
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsEvent
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsUiEvent
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
            showDateFormatSelectorDialog { dateFormat ->
                viewModel.onEvent(
                    SettingsEvent.ChangeDateFormat(
                        dateFormat = dateFormat.pattern
                    )
                )
            }
        }

        binding.btnTimeFormat.setOnClickListener {
            showTimeFormatSelectorDialog { timeFormat ->
                viewModel.onEvent(
                    SettingsEvent.ChangeTimeFormat(
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


    private fun showDateFormatSelectorDialog(onResult: (dateFormat: DateFormat) -> Unit) {
        val dialogOptions = arrayOf(
            parseCurrentTimeToString(DateFormat.YEAR_FIRST),
            parseCurrentTimeToString(DateFormat.MONTH_FIRST),
            parseCurrentTimeToString(DateFormat.DAY_FIRST),
        )
        val checkedItem = 0
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
                    0 -> onResult(DateFormat.YEAR_FIRST)
                    1 -> onResult(DateFormat.MONTH_FIRST)
                    2 -> onResult(DateFormat.DAY_FIRST)
                }
            }.show()
    }

    private fun showTimeFormatSelectorDialog(onResult: (timeFormat: String) -> Unit) {
        val dialogOptions = arrayOf(
            resources.getString(R.string.system_default),
            resources.getString(R.string.am_pm),
            resources.getString(R.string.normal_hour)
        )
        val checkedItem = 0
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
                        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(requireContext())
                        if (isSystem24Hour) onResult(sdfPattern24) else onResult(sdfPattern12)
                    }
                    1 -> onResult(sdfPattern12)
                    2 -> onResult(sdfPattern24)
                }
            }.show()
    }


    private fun parseCurrentTimeToString(format: DateFormat): String {
        val pattern = format.pattern
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance(Locale.getDefault())
        return sdf.format(calendar.time)
    }

    enum class DateFormat(val pattern: String) {
        YEAR_FIRST(pattern = "yyyy/MM/dd"),
        MONTH_FIRST(pattern = "MM/dd/yyyy"),
        DAY_FIRST(pattern = "dd/MM/yyyy"),
    }

    enum class TimeFormat(val format: Int) {
        AM_PM(format = CLOCK_12H),
        NORMAL(format = CLOCK_24H),
        SYSTEM_DEFAULT(format = 2)
    }
}