package com.yotfr.sunmoon.presentation.task

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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


@AndroidEntryPoint
class TaskRootFragment : Fragment(R.layout.fragment_task_root) {

    private lateinit var binding: FragmentTaskRootBinding

    private lateinit var scheduledTaskListFragment: ScheduledTaskListFragment
    private lateinit var unplannedTaskListFragment: UnplannedTaskListFragment
    private lateinit var outdatedTaskListFragment: OutdatedTaskFragment

    private var tabListener: TabLayout.OnTabSelectedListener? = null

    private val fragments: Array<Fragment>
        get() = arrayOf(
            scheduledTaskListFragment, unplannedTaskListFragment,
            outdatedTaskListFragment
        )
    private var selectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding = FragmentTaskRootBinding.bind(view)

        (requireActivity() as MainActivity).setUpActionBar(binding.fragmentTaskRootToolbar)


        tabListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectFragment(tab?.position!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }

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

        //TODO
        binding.fragmentTaskRootTabLayout.addOnTabSelectedListener(tabListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedIndex", selectedIndex)
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
        binding.fragmentTaskRootTabLayout.removeOnTabSelectedListener(
            tabListener
        )
    }
}