package com.yotfr.sunmoon.presentation.trash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentTrashRootBinding
import com.yotfr.sunmoon.presentation.MainActivity
import com.yotfr.sunmoon.presentation.trash.trash_note_list.TrashNoteFragment
import com.yotfr.sunmoon.presentation.trash.trash_task_list.TrashTaskFragment

class TrashRootFragment : Fragment(R.layout.fragment_trash_root) {

    private lateinit var binding: FragmentTrashRootBinding

    private lateinit var noteFragment: TrashNoteFragment
    private lateinit var taskFragment: TrashTaskFragment

    private val fragments: Array<Fragment> get() = arrayOf(noteFragment, taskFragment)
    private var selectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val noteFragment = TrashNoteFragment().also { this.noteFragment = it }
            val taskFragment = TrashTaskFragment().also { this.taskFragment = it }

            childFragmentManager.beginTransaction()
                .add(R.id.fragment_trash_root_fl, noteFragment, "noteFragment")
                .add(R.id.fragment_trash_root_fl, taskFragment, "taskFragment")
                .selectFragment(selectedIndex)
                .commit()
        } else {
            selectedIndex = savedInstanceState.getInt("selectedIndex", 0)

            noteFragment =
                childFragmentManager.findFragmentByTag("noteFragment") as TrashNoteFragment
            taskFragment =
                childFragmentManager.findFragmentByTag("taskFragment") as TrashTaskFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrashRootBinding.bind(view)

        //setUpActionBar
        (requireActivity() as MainActivity).setUpActionBar(binding.fragmentTrashRootToolbar)

        //change fragment from selected tab
        binding.fragmentTrashRootTabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectFragment(tab?.position!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
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
}