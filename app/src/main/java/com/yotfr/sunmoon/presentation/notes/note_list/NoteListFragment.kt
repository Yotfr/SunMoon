package com.yotfr.sunmoon.presentation.notes.note_list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentNoteListBinding
import com.yotfr.sunmoon.presentation.utils.onQueryTextChanged
import com.yotfr.sunmoon.presentation.notes.NoteRootFragmentDirections
import com.yotfr.sunmoon.presentation.notes.add_edit_note.BottomSheetAddEditNoteFragment
import com.yotfr.sunmoon.presentation.notes.note_list.adapter.*
import com.yotfr.sunmoon.presentation.notes.note_list.event.NoteListEvent
import com.yotfr.sunmoon.presentation.notes.note_list.event.NoteListUiEvent
import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListModel
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteListFragment : Fragment(R.layout.fragment_note_list) {

    companion object{
        private const val CHIP_HEADER_TAG = -1L
    }

    private val viewModel by viewModels<NoteListViewModel>()

    private lateinit var searchView: SearchView
    private lateinit var binding: FragmentNoteListBinding
    private lateinit var noteListAdapter: NoteListAdapter
    private lateinit var noteListFooterAdapter: NoteListFooterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNoteListBinding.bind(view)

        //inflateMenu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.app_bar_menu_list, menu)

                val searchItem = menu.findItem(R.id.mi_action_search)
                searchView = searchItem.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery.isNotEmpty()) {
                    searchItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }

                searchView.onQueryTextChanged {
                    viewModel.onEvent(
                        NoteListEvent.UpdateSearchQuery(
                            searchQuery = it
                        )
                    )
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_delete_all_tasks -> {
                        showDeleteAllDialog {
                            viewModel.onEvent(NoteListEvent.DeleteAllUnarchivedNote)
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        //initRvAdapters
        val noteLayoutManager = LinearLayoutManager(requireContext())
        noteListAdapter = NoteListAdapter()
        noteListAdapter.attachDelegate(object : NoteListDelegate {
            override fun noteDetailsClicked(noteId: Long) {
                navigateToEditNote(
                    noteId = noteId
                )
            }

            override fun pinPressed(note: NoteListModel) {
                viewModel.onEvent(
                    NoteListEvent.PinUnpinNote(
                        note = note
                    )
                )
            }
        })
        noteListFooterAdapter = NoteListFooterAdapter()

        val concatAdapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            noteListAdapter,
            noteListFooterAdapter
        )

        binding.rvNoteList.adapter = concatAdapter
        binding.rvNoteList.layoutManager = noteLayoutManager
        binding.rvNoteList.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )
        initSwipeToDelete()

        binding.fragmentNoteListCategoriesGroup.setOnCheckedStateChangeListener { group, _ ->
             val chipId = group.findViewById<Chip>(group.checkedChipId).tag as Long
            viewModel.onEvent(NoteListEvent.ChangeSelectedCategory(
                selectedCategoryId = chipId
            ))
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.noteListUiState.collect { noteState ->
                    noteState?.let { notes ->
                        noteListAdapter.notes = notes.notes
                        noteListFooterAdapter.footerState = notes.footerState
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryListUiState.collect { categoryState ->
                    categoryState?.let { categories ->
                        val chips:MutableList<Chip> = mutableListOf()
                        val headerChip = createHeaderChip()
                        categories.forEach {
                            chips.add(createCategoryChip(
                                text = it.categoryDescription,
                                categoryId = it.id
                            ))
                        }
                        binding.fragmentNoteListCategoriesGroup.removeAllViews()
                        binding.fragmentNoteListCategoriesGroup.addView(headerChip,0)
                        chips.forEach {
                            binding.fragmentNoteListCategoriesGroup.addView(
                                it
                            )
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is NoteListUiEvent.ShowUndoDeleteSnackbar -> {
                            showUndoDeleteSnackbar {
                                viewModel.onEvent(
                                    NoteListEvent.UndoDeleteNote(
                                        note = event.note
                                    )
                                )
                            }
                        }
                        is NoteListUiEvent.ShowUndoArchiveSnackbar -> {
                            showUndoArchiveSnackbar {
                                viewModel.onEvent(
                                    NoteListEvent.UndoArchiveNote(
                                        note = event.note
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val chipHeader =  binding.fragmentNoteListCategoriesGroup.findViewWithTag<Chip>(
            CHIP_HEADER_TAG
        )
        chipHeader.performClick()
    }

    private fun createHeaderChip():Chip{
        val chip = Chip(requireContext())
        val drawable = ChipDrawable.createFromAttributes(requireContext(),null,0,
        R.style.Widget_SimpleToDo_Chip_Choice_Primary)
        chip.apply {
            setChipDrawable(drawable)
            text = getString(R.string.all)
            tag = CHIP_HEADER_TAG
        }
        return chip
    }

    private fun createCategoryChip(text:String, categoryId:Long):Chip{
        val chip = Chip(requireContext())
        val drawable = ChipDrawable.createFromAttributes(requireContext(),null,0,
            R.style.Widget_SimpleToDo_Chip_Choice_Primary)
        chip.apply {
            setChipDrawable(drawable)
            setText(text)
            tag = categoryId
        }
        return chip
    }

    private fun showDeleteAllDialog(onPositive: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm_delete))
            .setMessage(resources.getString(R.string.sure_delete_note))
            .setNegativeButton(resources.getString(R.string.NO)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                onPositive()
            }.show()
    }

    private fun showUndoDeleteSnackbar(onAction: () -> Unit) {
        Snackbar.make(
            requireView(),
            getString(R.string.undo_delete_note_description),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo_delete_task_button_text)) {
                onAction()
            }.show()
    }

    private fun showUndoArchiveSnackbar(onAction: () -> Unit) {
        Snackbar.make(
            requireView(),
            getString(R.string.note_archived),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo_delete_task_button_text)) {
                onAction()
            }.show()
    }

    private fun navigateToEditNote(noteId: Long) {
        val direction =
            NoteRootFragmentDirections.actionNoteRootFragmentToBottomSheetAddEditNoteFragment(
                noteId = noteId,
                categoryId = BottomSheetAddEditNoteFragment.WITHOUT_CATEGORY_ID
            )
        findNavController().navigate(direction)
    }

    private fun initSwipeToDelete() {
        val onTrashItem = { positionToRemove: Int ->
            val note = noteListAdapter.notes[positionToRemove]
            viewModel.onEvent(
                NoteListEvent.DeleteNote(
                    note = note
                )
            )
        }
        val onArchiveItem = { positionToRemove: Int ->
            val note = noteListAdapter.notes[positionToRemove]
            viewModel.onEvent(
                NoteListEvent.ArchiveNote(
                    note = note
                )
            )
        }
        val noteListItemCallBack = NoteListItemCallback(
            onTrashItemNote = onTrashItem,
            onArchiveItemNote = onArchiveItem
        )
        ItemTouchHelper(noteListItemCallBack).attachToRecyclerView(binding.rvNoteList)
    }
}
