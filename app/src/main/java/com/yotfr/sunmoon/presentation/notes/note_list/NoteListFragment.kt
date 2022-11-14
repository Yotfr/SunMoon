package com.yotfr.sunmoon.presentation.notes.note_list

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class NoteListFragment : Fragment(R.layout.fragment_note_list) {

    companion object{
        //header category chipId
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

        addHeaderChipAndPerformClick()

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

        //on change selected chip
        binding.fragmentNoteListCategoriesGroup.setOnCheckedStateChangeListener { group, _ ->
             val chipId = group.findViewById<Chip>(group.checkedChipId).tag as Long
            Log.d("CHIP","$chipId")
            viewModel.onEvent(NoteListEvent.ChangeSelectedCategory(
                selectedCategoryId = chipId
            ))
        }

        //collect notes uiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.noteListUiState.collect { noteState ->
                    noteState?.let { notes ->
                        noteListAdapter.submitList(notes.notes)
                        noteListFooterAdapter.footerState = notes.footerState
                    }
                }
            }
        }

        //collect category uiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryListUiState.collect { categoryState ->
                    categoryState?.let { categories ->
                        //createChip for each collected category
                        val chips:MutableList<Chip> = mutableListOf()
                        withContext(Dispatchers.Default){
                            categories.forEach { category ->
                                chips.add(createCategoryChip(
                                    text = category.categoryDescription,
                                    categoryId = category.id
                                ))
                            }
                        }
                        //replace chips in chipGroup
                        binding.fragmentNoteListCategoriesGroup.removeViews(1,
                        binding.fragmentNoteListCategoriesGroup.childCount - 1)
                        chips.forEach { chip ->
                            binding.fragmentNoteListCategoriesGroup.addView(
                                chip
                            )
                        }
                    }
                }
            }
        }

        //collect uiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is NoteListUiEvent.ShowUndoDeleteSnackbar -> {
                            showUndoTrashNoteSnackbar {
                                viewModel.onEvent(
                                    NoteListEvent.UndoDeleteNote(
                                        note = event.note
                                    )
                                )
                            }
                        }
                        is NoteListUiEvent.ShowUndoArchiveSnackbar -> {
                            showUndoArchiveNoteSnackbar {
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

    //add header chip in chipGroup and performClick
    private fun addHeaderChipAndPerformClick(){
        binding.fragmentNoteListCategoriesGroup.addView(
            createHeaderChip()
        )
        val chipHeader =  binding.fragmentNoteListCategoriesGroup.findViewWithTag<Chip>(
            CHIP_HEADER_TAG
        )
        chipHeader.performClick()
    }

    //create headerChip
    private fun createHeaderChip():Chip{
        val chip = Chip(requireContext(),
        null,R.attr.PrimaryChipStyle)
        chip.apply {
            text = getString(R.string.all)
            tag = CHIP_HEADER_TAG
            isChecked = true
        }
        return chip
    }

    //create categoryChip
    private fun createCategoryChip(text:String, categoryId:Long):Chip{
        val chip = Chip(requireContext(),
            null,R.attr.PrimaryChipStyle)
        chip.apply {
            setText(text)
            tag = categoryId
        }
        return chip
    }

    private fun showDeleteAllDialog(onPositive: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm_delete_notes))
            .setMessage(resources.getString(R.string.notes_dialog_message))
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                onPositive()
            }.show()
    }

    //get current selected category to open addNoteFragment with it
    fun getCurrentSelectedCategory():Long{
        val selectedCategory = binding.fragmentNoteListCategoriesGroup.findViewById<Chip>(
            binding.fragmentNoteListCategoriesGroup.checkedChipId
        )
        Log.d("TEST","selCattext -> ${selectedCategory.text}")
        Log.d("TEST","selCattag -> ${selectedCategory.tag}")
        return selectedCategory.tag as Long
    }

    private fun showUndoTrashNoteSnackbar(onAction: () -> Unit) {
        Snackbar.make(
            requireView(),
            getString(R.string.note_trashed),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo)) {
                onAction()
            }.show()
    }

    private fun showUndoArchiveNoteSnackbar(onAction: () -> Unit) {
        Snackbar.make(
            requireView(),
            getString(R.string.note_archived),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo)) {
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

    //initialize itemTouchCallback
    private fun initSwipeToDelete() {
        val onTrashItem = { positionToRemove: Int ->
            val note = noteListAdapter.currentList[positionToRemove]
            viewModel.onEvent(
                NoteListEvent.DeleteNote(
                    note = note
                )
            )
        }
        val onArchiveItem = { positionToRemove: Int ->
            val note = noteListAdapter.currentList[positionToRemove]
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
