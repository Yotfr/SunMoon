package com.yotfr.sunmoon.presentation.notes.manage_categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentManageCategoriesBinding
import com.yotfr.sunmoon.presentation.notes.NoteRootFragmentDirections
import com.yotfr.sunmoon.presentation.notes.manage_categories.adapter.CategoriesDelegate
import com.yotfr.sunmoon.presentation.notes.manage_categories.adapter.ManageCategoriesAdapter
import com.yotfr.sunmoon.presentation.notes.manage_categories.adapter.ManageCategoriesFooterAdapter
import com.yotfr.sunmoon.presentation.notes.manage_categories.event.ManageCategoriesEvent
import com.yotfr.sunmoon.presentation.notes.manage_categories.model.ManageCategoriesModel
import com.yotfr.sunmoon.presentation.utils.MarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageCategoriesFragment : Fragment(R.layout.fragment_manage_categories) {

    private val viewModel by viewModels<ManageCategoriesViewModel>()

    private var _binding: FragmentManageCategoriesBinding? = null
    private val binding get()= _binding!!

    private lateinit var adapter: ManageCategoriesAdapter
    private lateinit var footerAdapter: ManageCategoriesFooterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentManageCategoriesBinding.bind(view)

        //initRvAdapters
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = ManageCategoriesAdapter()
        adapter.attachDelegate(object : CategoriesDelegate {
            override fun editCategoryPressed(category: ManageCategoriesModel) {
                navigateToEditCategory(
                    categoryId = category.id,
                    categoryDescription = category.categoryDescription
                )
            }

            override fun deleteCategoryPressed(category: ManageCategoriesModel) {
                showDeleteCategoryDialog {
                    viewModel.onEvent(
                        ManageCategoriesEvent.DeleteCategory(
                            category = category
                        )
                    )
                }
            }

            override fun changeCategoryVisibility(category: ManageCategoriesModel) {
                viewModel.onEvent(
                    ManageCategoriesEvent.ChangeCategoryVisibility(
                        category = category
                    )
                )
            }
        })

        footerAdapter = ManageCategoriesFooterAdapter()

        val concatAdapter = ConcatAdapter(
            ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build(),
            adapter,
            footerAdapter
        )

        binding.rvCategoryListManage.adapter = concatAdapter
        binding.rvCategoryListManage.layoutManager = layoutManager

        binding.rvCategoryListManage.addItemDecoration(
            MarginItemDecoration(
                spaceSize = resources.getDimensionPixelSize(R.dimen.default_margin)
            )
        )

        //collect uiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { categories ->
                    categories?.let {
                        adapter.submitList(it.categories)
                        footerAdapter.footerState = it.footerState
                    }
                }
            }
        }
    }

    private fun showDeleteCategoryDialog(onPositive: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm_delete_category))
            .setMessage(resources.getString(R.string.category_dialog_message))
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                onPositive()
            }.show()
    }

    private fun navigateToEditCategory(categoryId: Long, categoryDescription:String) {
        val direction = NoteRootFragmentDirections.actionGlobalAddCategoryDialogFragment(
            categoryId = categoryId,
            categoryDescription = categoryDescription
        )
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCategoryListManage.adapter = null
        _binding = null
    }
}