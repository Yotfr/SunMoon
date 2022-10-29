package com.yotfr.sunmoon.presentation.trash.trash_task_list

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
import com.yotfr.sunmoon.databinding.FragmentTrashTaskBinding
import com.yotfr.sunmoon.presentation.utils.onQueryTextChanged
import com.yotfr.sunmoon.presentation.trash.trash_task_list.adapter.*
import com.yotfr.sunmoon.presentation.trash.trash_task_list.event.TrashTaskEvent
import com.yotfr.sunmoon.presentation.trash.trash_task_list.event.TrashTaskUiEvent
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TrashTaskFragment : Fragment(R.layout.fragment_trash_task) {

    private lateinit var binding: FragmentTrashTaskBinding
    private lateinit var uncompletedTrashTaskAdapter: TrashedUncompletedTaskListAdapter
    private lateinit var completedTrashTaskAdapter: TrashedCompletedTaskAdapter
    private lateinit var completedTrashTaskHeaderAdapter: TrashedCompletedHeaderAdapter
    private lateinit var searchView: SearchView

    private val viewModel by viewModels<TrashTaskViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrashTaskBinding.bind(view)

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
                        TrashTaskEvent.UpdateSearchQuery(
                            searchQuery = it
                        )
                    )
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_delete_all_tasks -> {
                        showDeleteAllDialog {
                            when(it) {
                                DeleteOption.ALL_TRASHED -> {
                                    viewModel.onEvent(
                                        TrashTaskEvent.DeleteAllTrashedTask
                                    )

                                }
                                DeleteOption.COMPLETED_TRASHED -> {
                                    viewModel.onEvent(
                                        TrashTaskEvent.DeleteAllTrashedCompletedTask
                                    )
                                }
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        //initRvAdapters
        val linearLayoutManager = LinearLayoutManager(requireContext())
        uncompletedTrashTaskAdapter = TrashedUncompletedTaskListAdapter()
        uncompletedTrashTaskAdapter.attachDelegate(object : UncompletedTrashTaskDelegate {
            override fun taskItemPressed(taskId: Long) {
                //TODO
            }
        })
        completedTrashTaskAdapter = TrashedCompletedTaskAdapter()
        completedTrashTaskAdapter.attachDelegate(object : TrashedCompletedTaskListDelegate{
            override fun taskItemPressed(taskId: Long) {
                //TODO
            }
        })
        completedTrashTaskHeaderAdapter = TrashedCompletedHeaderAdapter()
        completedTrashTaskHeaderAdapter.attachDelegate(object :TrashedCompletedHeaderDelegate{
            override fun hideCompleted() {
               viewModel.onEvent(TrashTaskEvent.ChangeCompletedTasksVisibility)
            }
        })
        val concatAdapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            uncompletedTrashTaskAdapter,
            completedTrashTaskHeaderAdapter,
            completedTrashTaskAdapter
        )
        binding.fragmentTrashTaskRv.adapter = concatAdapter
        binding.fragmentTrashTaskRv.layoutManager = linearLayoutManager
        binding.fragmentTrashTaskRv.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )
        initSwipeToDelete()


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { taskState ->
                    taskState?.let { uiModel ->
                        completedTrashTaskAdapter.tasks = uiModel.completedTasks
                        uncompletedTrashTaskAdapter.deletedTasks = uiModel.uncompletedTasks
                        completedTrashTaskHeaderAdapter.headerState = uiModel.headerState
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        TrashTaskUiEvent.ShowRestoreSnackbar -> {
                            showRestoreSnackbar()
                        }
                        is TrashTaskUiEvent.ShowUndoDeleteSnackbar -> {
                            showUndoDeleteSnackBar {
                                viewModel.onEvent(
                                    TrashTaskEvent.UndoDeleteTrashedTask(
                                        uiEvent.task
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteAllDialog(onPositive: (deleteOption: DeleteOption) -> Unit) {
        val dialogOptions = arrayOf(
            resources.getString(R.string.all_trashed_completed),
            resources.getString(R.string.all_trashed)
        )
        val checkedItem = 0
        var selectedItem = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.choose_delete_options))
            .setSingleChoiceItems(dialogOptions, checkedItem) { _, which ->
                selectedItem = which
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                when (selectedItem) {
                    0 -> onPositive(DeleteOption.COMPLETED_TRASHED)
                    else -> onPositive(DeleteOption.ALL_TRASHED)
                }
            }.show()
    }

    private fun showUndoDeleteSnackBar(onAction: () -> Unit){
        Snackbar.make(
            requireView(),
            getString(R.string.task_deleted),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo_delete_task_button_text)) {
                onAction()
            }.show()
    }

    private fun showRestoreSnackbar(){
        Snackbar.make(
            requireView(),
            getString(R.string.task_restored),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun initSwipeToDelete() {
        val onUncompletedItemRemoved = { positionToRemove: Int ->
            val task = uncompletedTrashTaskAdapter.deletedTasks[positionToRemove]
            viewModel.onEvent(TrashTaskEvent.DeleteTrashedTask(
                task = task
            ))
        }
        val onCompletedItemRemoved = { positionToRemove: Int ->
            val task = completedTrashTaskAdapter.tasks[positionToRemove]
            viewModel.onEvent(TrashTaskEvent.DeleteTrashedTask(
                task = task
            ))
        }
        val onUncompletedItemRestored = { positionToRemove: Int ->
            val task = uncompletedTrashTaskAdapter.deletedTasks[positionToRemove]
            viewModel.onEvent(TrashTaskEvent.RestoreTrashedTask(
                task = task
            ))
        }
        val onCompletedItemRestored = { positionToRemove: Int ->
            val task = completedTrashTaskAdapter.tasks[positionToRemove]
            viewModel.onEvent(TrashTaskEvent.RestoreTrashedTask(
                task = task
            ))
        }
        val trashedTaskListItemCallback = TrashedTaskListItemCallback(
            onUncompletedItemDelete = onUncompletedItemRemoved,
            onUncompletedItemRestore = onUncompletedItemRestored,
            onCompletedItemDelete = onCompletedItemRemoved,
            onCompletedItemRestore = onCompletedItemRestored
        )
        ItemTouchHelper(trashedTaskListItemCallback).attachToRecyclerView(binding.fragmentTrashTaskRv)
    }

    enum class DeleteOption {
        ALL_TRASHED, COMPLETED_TRASHED
    }
}