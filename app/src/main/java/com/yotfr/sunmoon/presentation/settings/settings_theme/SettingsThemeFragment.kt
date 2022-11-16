package com.yotfr.sunmoon.presentation.settings.settings_theme

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
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
class SettingsThemeFragment : Fragment(R.layout.fragment_theme_picker) {

    private var _binding: FragmentThemePickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingsThemeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentThemePickerBinding.bind(view)

        binding.radioGroup.findViewWithTag<RadioButton>(viewModel.themeState.value).isChecked = true

        //setUp actionBar
        (requireActivity() as MainActivity).setUpActionBar(binding.themeToolbar)

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onEvent(
                SettingsThemeEvent.ChangeTheme(
                    newTheme = group.findViewById<RadioButton>(checkedId).tag as String
                )
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is SettingsThemeUiEvent.RestartActivity -> {
                            //restart activity when new theme picked
                            requireActivity().recreate()
                        }
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}