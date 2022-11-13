package com.yotfr.sunmoon.presentation.notes.archive_note

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentArchiveNoteBinding
import com.yotfr.sunmoon.presentation.utils.onQueryTextChanged
import com.yotfr.sunmoon.presentation.notes.NoteRootFragmentDirections
import com.yotfr.sunmoon.presentation.notes.add_edit_note.BottomSheetAddEditNoteFragment
import com.yotfr.sunmoon.presentation.notes.archive_note.adapter.ArchiveNoteAdapter
import com.yotfr.sunmoon.presentation.notes.archive_note.adapter.ArchiveNoteListDelegate
import com.yotfr.sunmoon.presentation.notes.archive_note.adapter.ArchiveNoteListFooterAdapter
import com.yotfr.sunmoon.presentation.notes.archive_note.adapter.ArchiveNoteListItemCallback
import com.yotfr.sunmoon.presentation.notes.archive_note.event.ArchiveNoteEvent
import com.yotfr.sunmoon.presentation.notes.archive_note.event.ArchiveNoteUiEvent
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArchiveNoteFragment : Fragment(R.layout.fragment_archive_note) {

    private val viewModel by viewModels<ArchiveNoteViewModel>()

    private lateinit var searchView: SearchView

    private lateinit var binding: FragmentArchiveNoteBinding

    private lateinit var archiveNoteListAdapter: ArchiveNoteAdapter
    private lateinit var archiveNoteListFooterAdapter: ArchiveNoteListFooterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArchiveNoteBinding.bind(view)

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
                        ArchiveNoteEvent.UpdateSearchQuery(
                            searchQuery = it
                        )
                    )
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_delete_all_tasks -> {
                        showDeleteAllDialog {
                            viewModel.onEvent(ArchiveNoteEvent.DeleteAllUnarchivedNote)
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        //initRvAdapter
        val archiveNoteLayoutManager = LinearLayoutManager(requireContext())
        archiveNoteListAdapter = ArchiveNoteAdapter()
        archiveNoteListAdapter.attachDelegate(object : ArchiveNoteListDelegate {
            override fun noteDetailsClicked(id: Long) {
                navigateToAddEditNote(
                    noteId = id
                )
            }
        })
        archiveNoteListFooterAdapter = ArchiveNoteListFooterAdapter()

        val concatAdapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            archiveNoteListAdapter,
            archiveNoteListFooterAdapter
        )

        binding.rvArchiveNote.adapter = concatAdapter
        binding.rvArchiveNote.layoutManager = archiveNoteLayoutManager

        binding.rvArchiveNote.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )

        initSwipeToDelete()

        //collect uiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    uiState?.let { notes ->
                        archiveNoteListAdapter.submitList(notes.notes)
                        archiveNoteListFooterAdapter.footerState = notes.footerState
                    }
                }
            }
        }

        //collect uiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is ArchiveNoteUiEvent.ShowUndoDeleteSnackbar -> {
                            showUndoTrashNoteSnackbar {
                                viewModel.onEvent(
                                    ArchiveNoteEvent.UndoDeleteArchiveNote(
                                        note = event.note
                                    )
                                )
                            }
                        }
                        ArchiveNoteUiEvent.ShowUnarchiveSnackbar ->  {
                            showUnarchiveNoteSnackbar()
                        }
                    }
                }
            }
        }
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

    //initialize itemTouchCallback
    private fun initSwipeToDelete() {
        val onTrashItem = { positionToRemove: Int ->
            val note = archiveNoteListAdapter.currentList[positionToRemove]
            viewModel.onEvent(
                ArchiveNoteEvent.DeleteArchiveNote(
                    note = note
                )
            )
        }
        val onUnarchiveItem = { positionToRemove: Int ->
            val note = archiveNoteListAdapter.currentList[positionToRemove]
            viewModel.onEvent(
                ArchiveNoteEvent.UnarchiveArchiveNote(
                    note = note
                )
            )
        }
        val archiveNoteListItemCallBack = ArchiveNoteListItemCallback(
            onTrashItemNote = onTrashItem,
            onUnarchiveItemNote = onUnarchiveItem
        )
        ItemTouchHelper(archiveNoteListItemCallBack).attachToRecyclerView(binding.rvArchiveNote)
    }

    private fun showUnarchiveNoteSnackbar(){
        Snackbar.make(
            requireView(),
            getString(R.string.note_unarchived),
            Snackbar.LENGTH_LONG
        ).show()
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

    private fun navigateToAddEditNote(noteId: Long) {
        val direction =
            NoteRootFragmentDirections.actionNoteRootFragmentToBottomSheetAddEditNoteFragment(
                noteId = noteId,
                categoryId = BottomSheetAddEditNoteFragment.WITHOUT_CATEGORY_ID
            )
        findNavController().navigate(direction)
    }
}