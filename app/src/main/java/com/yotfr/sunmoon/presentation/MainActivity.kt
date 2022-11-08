package com.yotfr.sunmoon.presentation

import android.os.Bundle
import android.util.Log
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
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
                when(dataStoreRepository.getTheme()) {
                    "orange" -> {
                        theme.applyStyle(R.style.OverlayThemeOrangeSunMoon, true)
                    }
                    "pink" -> {
                        theme.applyStyle(R.style.OverlayThemeGreenSunMoon, true)
                    }
                    "yellow" -> {
                        theme.applyStyle(R.style.OverlayThemeOrangeSunMoon, true)
                    }
                    "night" -> {
                        theme.applyStyle(R.style.OverlayThemeNightSunMoon, true)
                    }
                    else -> {
                        theme.applyStyle(R.style.OverlayThemeOrangeSunMoon, true)
                    }
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