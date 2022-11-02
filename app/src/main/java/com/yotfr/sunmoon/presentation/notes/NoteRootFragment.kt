package com.yotfr.sunmoon.presentation.notes

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentNoteRootBinding
import com.yotfr.sunmoon.presentation.MainActivity
import com.yotfr.sunmoon.presentation.notes.archive_note.ArchiveNoteFragment
import com.yotfr.sunmoon.presentation.notes.manage_categories.ManageCategoriesFragment
import com.yotfr.sunmoon.presentation.notes.note_list.NoteListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteRootFragment : Fragment(R.layout.fragment_note_root) {

    private lateinit var binding: FragmentNoteRootBinding

    private lateinit var noteListFragment: NoteListFragment
    private lateinit var archiveNoteFragment: ArchiveNoteFragment
    private lateinit var manageCategoriesFragment: ManageCategoriesFragment

    private var tabListener: TabLayout.OnTabSelectedListener? = null

    private val fragments: Array<Fragment>
        get() = arrayOf(
            noteListFragment, archiveNoteFragment,
            manageCategoriesFragment
        )
    private var selectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val noteListFragment =
                NoteListFragment().also { this.noteListFragment = it }
            val archiveNoteFragment =
                ArchiveNoteFragment().also { this.archiveNoteFragment = it }
            val manageCategoriesFragment =
                ManageCategoriesFragment().also { this.manageCategoriesFragment = it }

            childFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_note_rool_fl,
                    noteListFragment,
                    "noteListFragment"
                )
                .add(
                    R.id.fragment_note_rool_fl,
                    archiveNoteFragment,
                    "archiveNoteFragment"
                )
                .add(
                    R.id.fragment_note_rool_fl,
                    manageCategoriesFragment,
                    "manageCategoriesFragment"
                )
                .selectFragment(selectedIndex)
                .commit()
        } else {
            selectedIndex = savedInstanceState.getInt("selectedIndex", 0)

            noteListFragment =
                childFragmentManager.findFragmentByTag("noteListFragment")
                        as NoteListFragment
            archiveNoteFragment =
                childFragmentManager.findFragmentByTag("archiveNoteFragment")
                        as ArchiveNoteFragment
            manageCategoriesFragment =
                childFragmentManager.findFragmentByTag("manageCategoriesFragment")
                        as ManageCategoriesFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNoteRootBinding.bind(view)

        (requireActivity() as MainActivity).setUpActionBar(binding.fragmentNoteRootToolbar)


        tabListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectFragment(tab?.position!!)
                when(tab.position) {
                    0 -> {
                        binding.fragmentNoteRootFab.show()
                    }
                    1 -> {
                        binding.fragmentNoteRootFab.hide()
                    }
                    2 -> {
                        binding.fragmentNoteRootFab.show()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }

        binding.fragmentNoteRootScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY && binding.fragmentNoteRootFab.isShown) {
                    binding.fragmentNoteRootFab.hide()
                }

                if (scrollY < oldScrollY && !binding.fragmentNoteRootFab.isShown) {
                    binding.fragmentNoteRootFab.show()
                }

                if (scrollY == 0) {
                    binding.fragmentNoteRootFab.show()
                }
            }
        )

        binding.fragmentNoteRootFab.setOnClickListener {
            when (selectedIndex) {
                2 -> {
                    navigateToDestination(Destination.ADD_CATEGORY)
                }
                else -> {
                    navigateToDestination(Destination.ADD_NOTE)
                }
            }
        }

        //TODO
        binding.fragmentNoteRootTabLayout.addOnTabSelectedListener(tabListener)
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


    private fun navigateToDestination(destination: Destination) {
        val direction: NavDirections = when (destination) {
            Destination.ADD_NOTE -> {
                NoteRootFragmentDirections.actionNoteRootFragmentToBottomSheetAddEditNoteFragment(
                    noteId = -1L,
                    categoryId = -1L
                )
            }
            Destination.ADD_CATEGORY -> {
                NoteRootFragmentDirections.actionGlobalAddCategoryDialogFragment(
                    categoryId = -1L,
                    ""
                )
            }
        }
        findNavController().navigate(direction)
    }

    enum class Destination {
        ADD_NOTE, ADD_CATEGORY
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.fragmentNoteRootTabLayout.removeOnTabSelectedListener(
            tabListener
        )
    }
}