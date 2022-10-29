package com.yotfr.sunmoon.presentation.notes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yotfr.sunmoon.presentation.notes.archive_note.ArchiveNoteFragment
import com.yotfr.sunmoon.presentation.notes.manage_categories.ManageCategoriesFragment
import com.yotfr.sunmoon.presentation.notes.note_list.NoteListFragment

private const val NUM_TABS = 3

class NoteViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 ->   NoteListFragment()
            1 ->  ArchiveNoteFragment()
            2 ->  ManageCategoriesFragment()
            else -> throw IllegalArgumentException("")
        }
    }
}