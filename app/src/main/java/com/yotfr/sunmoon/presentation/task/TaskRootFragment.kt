package com.yotfr.sunmoon.presentation.task

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentTaskRootBinding
import com.yotfr.sunmoon.presentation.MainActivity
import com.yotfr.sunmoon.presentation.task.add_task.BottomSheetAddTaskFragment
import com.yotfr.sunmoon.presentation.task.outdated_task_list.OutdatedTaskFragment
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.ScheduledTaskListFragment
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.UnplannedTaskListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskRootFragment : Fragment(R.layout.fragment_task_root) {

    companion object {
        //selectedDate coming from taskDetails or addTask
        const val SELECTED_TASK_DATE = "SELECTED_TASK_DATE"
        //null selectedDate coming from taskDetails or addTask
        const val WITHOUT_TASK_DATE = 0L
    }

    private var _binding: FragmentTaskRootBinding? =null
    private val binding get() = _binding!!

    private lateinit var scheduledTaskListFragment: ScheduledTaskListFragment
    private lateinit var unplannedTaskListFragment: UnplannedTaskListFragment
    private lateinit var outdatedTaskListFragment: OutdatedTaskFragment

    private val fragments: Array<Fragment>
        get() = arrayOf(
            scheduledTaskListFragment, unplannedTaskListFragment,
            outdatedTaskListFragment
        )
    private var selectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //remove backStack StateFlow value
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long?>(
            SELECTED_TASK_DATE
        )

        if (savedInstanceState == null) {
            val scheduledTaskListFragment =
                ScheduledTaskListFragment().also { this.scheduledTaskListFragment = it }
            val unplannedTaskListFragment =
                UnplannedTaskListFragment().also { this.unplannedTaskListFragment = it }
            val outdatedTaskListFragment =
                OutdatedTaskFragment().also { this.outdatedTaskListFragment = it }

            childFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_task_root_fl,
                    scheduledTaskListFragment,
                    "scheduledTaskListFragment"
                )
                .add(
                    R.id.fragment_task_root_fl,
                    unplannedTaskListFragment,
                    "unplannedTaskListFragment"
                )
                .add(
                    R.id.fragment_task_root_fl,
                    outdatedTaskListFragment,
                    "outdatedTaskListFragment"
                )
                .selectFragment(selectedIndex)
                .commit()
        } else {
            selectedIndex = savedInstanceState.getInt("selectedIndex", 0)

            scheduledTaskListFragment =
                childFragmentManager.findFragmentByTag("scheduledTaskListFragment")
                        as ScheduledTaskListFragment
            unplannedTaskListFragment =
                childFragmentManager.findFragmentByTag("unplannedTaskListFragment")
                        as UnplannedTaskListFragment
            outdatedTaskListFragment =
                childFragmentManager.findFragmentByTag("outdatedTaskListFragment")
                        as OutdatedTaskFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTaskRootBinding.bind(view)

        //setUpActionBar
        (requireActivity() as MainActivity).setUpActionBar(binding.fragmentTaskRootToolbar)

        //change fragment from selected tab
        binding.fragmentTaskRootTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectFragment(tab?.position!!)
                when (tab.position) {
                    0 -> {
                        binding.fragmentTaskRootFab.show()
                    }
                    1 -> {
                        binding.fragmentTaskRootFab.show()
                    }
                    2 -> {
                        binding.fragmentTaskRootFab.hide()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        //collect backStack selectedDate from taskDetails or addTask and change tab
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val navController = findNavController()
                navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Long?>(
                    SELECTED_TASK_DATE, null
                )?.collect { date ->
                    Log.d("TEST","date -> $date")
                    when(date) {
                        WITHOUT_TASK_DATE -> {
                            changeTabFromChildFragment(
                                fragmentPosition = 1
                            )
                        }
                        null -> {}
                        else -> {
                            changeTabFromChildFragment(
                                fragmentPosition = 0,
                                selectedTaskDate = date
                            )
                        }
                    }
                }
            }
        }

        //hide/show fab on scroll  in childFragments
        binding.fragmentTaskRootScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY && binding.fragmentTaskRootFab.isShown) {
                    binding.fragmentTaskRootFab.hide()
                }
                if (scrollY < oldScrollY && !binding.fragmentTaskRootFab.isShown) {
                    binding.fragmentTaskRootFab.show()
                }
                if (scrollY == 0) {
                    binding.fragmentTaskRootFab.show()
                }
            }
        )

        //choose fab click destination depends on current selected fragment
        binding.fragmentTaskRootFab.setOnClickListener {
            when (selectedIndex) {
                0 -> {
                    navigateToAddTask(
                        selectedDate = scheduledTaskListFragment.getCurrentSelectedDate()
                    )
                }
                else -> {
                    navigateToAddTask()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        changeSelectedTab()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedIndex", selectedIndex)
    }

    //public toAccess from childFragments
    fun changeTabFromChildFragment(
        fragmentPosition: Int,
        selectedTaskDate: Long? = null
    ) {
        if (fragmentPosition == 0) {
            selectFragment(0)
            scheduledTaskListFragment.selectCalendarDate(
                selectedTaskDate = selectedTaskDate ?: throw IllegalArgumentException(
                    "Trying to navigate to scheduledTasks without task scheduled date"
                )
            )
            changeSelectedTab()
            return
        }
        selectFragment(fragmentPosition)
    }

    //change selected tab
    private fun changeSelectedTab(){
        binding.fragmentTaskRootTabLayout.selectTab(
            binding.fragmentTaskRootTabLayout.getTabAt(selectedIndex)
        )
    }

    private fun FragmentTransaction.selectFragment(selectedIndex: Int): FragmentTransaction {
        fragments.forEachIndexed { index, fragment ->
            if (index == selectedIndex) {
                attach(fragment)
            } else {
                detach(fragment)
            }
        }
        return this
    }

    private fun selectFragment(indexToSelect: Int) {
        this.selectedIndex = indexToSelect
        childFragmentManager.beginTransaction()
            .selectFragment(indexToSelect)
            .commit()
    }

    private fun navigateToAddTask(selectedDate: Long? = null) {
        val direction =
            TaskRootFragmentDirections.actionTaskRootFragmentToBottomSheetAddTaskFragment(
                selectedDate = selectedDate ?: BottomSheetAddTaskFragment.WITHOUT_SELECTED_DATE
            )
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}