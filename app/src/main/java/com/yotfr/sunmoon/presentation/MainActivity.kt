package com.yotfr.sunmoon.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.material.appbar.MaterialToolbar
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ActivityMainBinding
import com.yotfr.sunmoon.domain.repository.sharedpreference.PreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: PreferencesHelper

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (sharedPreferences.getTheme()) {
            "orange" -> {
                theme.applyStyle(R.style.OverlayThemeOrange, true)
            }
            "pink" -> {
                theme.applyStyle(R.style.OverlayThemePink, true)
            }
            "yellow" -> {
                theme.applyStyle(R.style.OverlayThemeYellow, true)
            }
            "night" -> {
                theme.applyStyle(R.style.OverlayThemeNight, true)
            }
            else -> {
                theme.applyStyle(R.style.OverlayThemeOrange, true)
            }
        }


        val binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        val topLevelDestinations = setOf(
            R.id.noteRootFragment, R.id.taskRootFragment, R.id.trashRootFragment,
            R.id.settingsFragment
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.activity_main_fragment_container
            ) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfiguration = AppBarConfiguration(topLevelDestinations, binding.activityMainDrawer)
        NavigationUI.setupWithNavController(binding.activityMainNavView, navController)


    }

    fun setUpActionBar(actionBar: MaterialToolbar) {
        setSupportActionBar(actionBar)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}