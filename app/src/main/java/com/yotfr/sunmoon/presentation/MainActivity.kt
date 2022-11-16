package com.yotfr.sunmoon.presentation

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.material.appbar.MaterialToolbar
import com.yotfr.sunmoon.ContextUtils
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.data.repository.DataStoreRepositoryImpl
import com.yotfr.sunmoon.databinding.ActivityMainBinding
import com.yotfr.sunmoon.domain.repository.data_store.DataStoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun attachBaseContext(newBase: Context) {
        var localeToSwitchTo: Locale
        runBlocking {
            localeToSwitchTo = Locale(
                DataStoreRepositoryImpl(
                    context = newBase
                ).getLanguage()
            )
        }
        Log.d("TEST","attach -> $localeToSwitchTo")
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase,
            localeToSwitchTo
        )
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
                when(dataStoreRepository.getTheme()) {
                    "orange" -> {
                        theme.applyStyle(R.style.OverlayThemeOrangeSunMoon, true)
                    }
                    "green" -> {
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
        navController = navHostFragment.navController
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