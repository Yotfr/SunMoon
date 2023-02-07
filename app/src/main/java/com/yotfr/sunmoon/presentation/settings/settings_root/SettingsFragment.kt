package com.yotfr.sunmoon.presentation.settings.settings_root

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentSettingsBinding
import com.yotfr.sunmoon.presentation.MainActivity
import com.yotfr.sunmoon.presentation.settings.settings_root.event.SettingsEvent
import com.yotfr.sunmoon.presentation.settings.settings_root.model.DatePattern
import com.yotfr.sunmoon.presentation.settings.settings_root.model.LanguageCode
import com.yotfr.sunmoon.presentation.settings.settings_root.model.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        // navigate to change theme fragment
        binding.btnChangeTheme.setOnClickListener {
            navigateToThemePicker()
        }

        // In project every fragment uses its own toolbar, but but there are fragments with tabs
        // which contains child fragments, since tabs are included in the toolbar, i decided to
        // use activity owned toolbar and change toolbar from fragment using this activity method
        (requireActivity() as MainActivity).setUpActionBar(binding.fragmentSettingsToolbar)

        // show dateFormat picker dialog
        binding.btnDateFormat.setOnClickListener {
            showDateFormatSelectorDialog(
                viewModel.dateTimeUiState.value.datePattern
            ) { datePattern ->
                viewModel.onEvent(
                    SettingsEvent.ChangeDateFormat(
                        dateFormat = datePattern.pattern
                    )
                )
            }
        }

        // show timeFormat picker dialog
        binding.btnTimeFormat.setOnClickListener {
            showTimeFormatSelectorDialog(
                viewModel.dateTimeUiState.value.timeFormat
            ) { timePattern, timeFormat ->
                viewModel.onEvent(
                    SettingsEvent.ChangeTimeFormat(
                        timePattern = timePattern,
                        timeFormat = timeFormat
                    )
                )
            }
        }
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
            parseCurrentTimeToString(DatePattern.DAY_FIRST)
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

    private fun showLanguageSelectorDialog(
        currentLanguage: LanguageCode,
        onResult: (languageCode: LanguageCode) -> Unit
    ) {
        val languageOptions = arrayOf(
            getString(R.string.english),
            getString(R.string.russian)
        )

        val checkedItem = languageOptions.indexOf(
            when (currentLanguage) {
                LanguageCode.RUSSIAN -> {
                    getString(R.string.russian)
                }
                LanguageCode.ENGLISH -> {
                    getString(R.string.english)
                }
            }
        )
        var selectedItem = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.select_date_format))
            .setSingleChoiceItems(languageOptions, checkedItem) { _, which ->
                selectedItem = which
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.save)) { _, _ ->
                when (selectedItem) {
                    0 -> onResult(LanguageCode.ENGLISH)
                    1 -> onResult(LanguageCode.RUSSIAN)
                }
            }.show()
    }

    private fun showTimeFormatSelectorDialog(
        currentTimeFormat: TimeFormat,
        onResultPattern: (timePattern: String, timeFormat: Int) -> Unit
    ) {
        val dialogOptions = arrayOf(
            resources.getString(R.string.system_default),
            resources.getString(R.string.am_pm),
            resources.getString(R.string.normal_hour)
        )
        val checkedItem = when (currentTimeFormat) {
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
            .setTitle(resources.getString(R.string.select_time_format))
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
                        if (isSystem24Hour) {
                            onResultPattern(sdfPattern24, TimeFormat.SYSTEM_DEFAULT.format)
                        } else onResultPattern(
                            sdfPattern12,
                            TimeFormat.SYSTEM_DEFAULT.format
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
