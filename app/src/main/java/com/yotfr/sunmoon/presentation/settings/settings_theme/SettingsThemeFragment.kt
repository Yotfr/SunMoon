package com.yotfr.sunmoon.presentation.settings.settings_theme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentThemePickerBinding
import com.yotfr.sunmoon.presentation.MainActivity
import com.yotfr.sunmoon.presentation.settings.settings_theme.event.SettingsThemeEvent
import com.yotfr.sunmoon.presentation.settings.settings_theme.event.SettingsThemeUiEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsThemeFragment: Fragment(R.layout.fragment_theme_picker) {

    private lateinit var binding: FragmentThemePickerBinding

    private val viewModel by viewModels<SettingsThemeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentThemePickerBinding.bind(view)

        (requireActivity() as MainActivity).setUpActionBar(binding.fragmentThemeToolbar)

        binding.apply {
            btnThemeNight.setOnClickListener {
                viewModel.onEvent(SettingsThemeEvent.ChangeTheme(
                    newTheme = "night"
                ))
            }
            btnThemeOrange.setOnClickListener {
                viewModel.onEvent(SettingsThemeEvent.ChangeTheme(
                    newTheme = "orange"
                ))
            }
            btnThemePink.setOnClickListener {
                viewModel.onEvent(SettingsThemeEvent.ChangeTheme(
                    newTheme = "pink"
                ))
            }
            btnThemeYellow.setOnClickListener {
                viewModel.onEvent(SettingsThemeEvent.ChangeTheme(
                    newTheme = "yellow"
                ))
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect{ uiEvent ->
                    when(uiEvent) {
                        is SettingsThemeUiEvent.RestartActivity -> {
                            requireActivity().recreate()
                        }
                    }
                }
            }
        }


    }
}