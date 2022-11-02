package com.yotfr.sunmoon.presentation.trash.trash_note_list

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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentTrashNoteBinding
import com.yotfr.sunmoon.presentation.utils.onQueryTextChanged
import com.yotfr.sunmoon.presentation.trash.trash_note_list.adapter.TrashNoteDelegate
import com.yotfr.sunmoon.presentation.trash.trash_note_list.adapter.TrashNoteFooterAdapter
import com.yotfr.sunmoon.presentation.trash.trash_note_list.adapter.TrashNoteListItemCallback
import com.yotfr.sunmoon.presentation.trash.trash_note_list.adapter.TrashNotesAdapter
import com.yotfr.sunmoon.presentation.trash.trash_note_list.event.TrashNoteEvent
import com.yotfr.sunmoon.presentation.trash.trash_note_list.event.TrashNoteUiEvent
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TrashNoteFragment : Fragment(R.layout.fragment_trash_note) {

    private lateinit var searchView: SearchView
    private lateinit var binding: FragmentTrashNoteBinding
    private lateinit var trashNotesAdapter: TrashNotesAdapter
    private lateinit var trashNotesFooterAdapter: TrashNoteFooterAdapter

    private val viewModel by viewModels<TrashNoteViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrashNoteBinding.bind(view)


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
                        TrashNoteEvent.UpdateSearchQuery(
                            searchQuery = it
                        )
                    )
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_delete_all_tasks -> {
                        showDeleteAllDialog {
                            viewModel.onEvent(
                                TrashNoteEvent.DeleteAllTrashedNotes
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)



        //initRvAdapters
        val linearLayoutManager = LinearLayoutManager(requireContext())
        trashNotesAdapter = TrashNotesAdapter()
        trashNotesAdapter.attachDelegate(object : TrashNoteDelegate {
            override fun trashNoteClicked(noteId: Long) {
                //TODO
            }
        })

        trashNotesFooterAdapter = TrashNoteFooterAdapter()

        val concatAdapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            trashNotesAdapter,
            trashNotesFooterAdapter
        )

        binding.fragmentTrashNoteRv.adapter = concatAdapter
        binding.fragmentTrashNoteRv.layoutManager = linearLayoutManager
        binding.fragmentTrashNoteRv.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )
        initSwipeToDelete()


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { noteState ->
                    noteState?.let { notes ->
                        trashNotesAdapter.deletedNotes = notes.notes
                        trashNotesFooterAdapter.footerState = notes.footerState
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is TrashNoteUiEvent.ShowRestoreSnackbar -> {
                            showRestoreSnackbar()
                        }
                        is TrashNoteUiEvent.ShowUndoDeleteSnackbar -> {
                            showUndoDeleteSnackbar {
                                viewModel.onEvent(
                                    TrashNoteEvent.UndoDeleteTrashedNote(
                                        note = uiEvent.note
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
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

    private fun showRestoreSnackbar() {
        Snackbar.make(
            requireView(),
            getString(R.string.note_restored),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showDeleteAllDialog(onPositive: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm_delete))
            .setMessage(resources.getString(R.string.sure_delete_trashed_note))
            .setNegativeButton(resources.getString(R.string.NO)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                onPositive()
            }
            .show()
    }

    private fun initSwipeToDelete() {
        val onItemRemoved = { positionToRemove: Int ->
            val note = trashNotesAdapter.deletedNotes[positionToRemove]
            viewModel.onEvent(TrashNoteEvent.DeleteTrashedNote(
                note = note
            ))
        }
        val onItemRestored = { positionToRemove: Int ->
            val note = trashNotesAdapter.deletedNotes[positionToRemove]
            viewModel.onEvent(TrashNoteEvent.RestoreTrashedNote(
                note = note
            ))
        }
        val trashedNoteListItemCallback = TrashNoteListItemCallback(
            onItemDelete = onItemRemoved,
            onItemRestore = onItemRestored
        )
        ItemTouchHelper(trashedNoteListItemCallback).attachToRecyclerView(binding.fragmentTrashNoteRv)
    }
}