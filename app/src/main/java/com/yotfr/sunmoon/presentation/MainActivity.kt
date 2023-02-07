package com.yotfr.sunmoon.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.material.appbar.MaterialToolbar
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ActivityMainBinding
import com.yotfr.sunmoon.domain.repository.datastore.DataStoreRepository
import com.yotfr.sunmoon.presentation.utils.ActionBarOnDestinationChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var destinationChangedListener: ActionBarOnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // to ensure that apply theme executes before setContentView
        runBlocking {
            when (dataStoreRepository.getTheme()) {
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
            R.id.noteRootFragment,
            R.id.taskRootFragment,
            R.id.trashRootFragment,
            R.id.settingsFragment
        )
        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.activity_main_fragment_container
            ) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(topLevelDestinations, binding.activityMainDrawer)
        NavigationUI.setupWithNavController(binding.activityMainNavView, navController)
        destinationChangedListener = ActionBarOnDestinationChangedListener(
            this,
            appBarConfiguration
        )
    }

    // In project every fragment uses its own toolbar, but but there are fragments with tabs
    // which contains child fragments, since tabs are included in the toolbar, i decided to
    // use activity owned toolbar and change toolbar from fragment using this activity method
    fun setUpActionBar(actionBar: MaterialToolbar?) {
        setSupportActionBar(actionBar)
        // NavigationUI.setUpActionBarWithNavController with every call adds new
        // ActionBarOnDestinationChangedListener to navController
        // This leads to memoryLeak each fragment that uses this method
        // So i decided to replace it, i created ActionBarOnDestinationChangedListener class because
        // NavigationUi.ActionBarOnDestinationChangedListener is internal and i cannot use it
        // And then i create this listener in activity, initialize it in onCreate
        // and add it to navController. This ensures that when this method is called, a new instance
        // of ActionBarOnDestinationChangedListener will not be generated.
        // Memory leak problem is solved
        navController.addOnDestinationChangedListener(
            destinationChangedListener
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
