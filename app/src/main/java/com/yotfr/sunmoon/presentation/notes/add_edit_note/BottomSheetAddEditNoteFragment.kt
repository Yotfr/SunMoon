package com.yotfr.sunmoon.presentation.notes.add_edit_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListPopupWindow
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.FragmentBottomSheetAddEditNoteBinding
import com.yotfr.sunmoon.presentation.notes.NoteRootFragmentDirections
import com.yotfr.sunmoon.presentation.notes.add_category.AddCategoryDialogFragment
import com.yotfr.sunmoon.presentation.notes.add_edit_note.adapter.PopUpDelegate
import com.yotfr.sunmoon.presentation.notes.add_edit_note.adapter.PopUpMenuAdapter
import com.yotfr.sunmoon.presentation.notes.add_edit_note.event.AddEditNoteEvent
import com.yotfr.sunmoon.presentation.notes.add_edit_note.event.AddEditNoteUiEvent
import com.yotfr.sunmoon.presentation.notes.add_edit_note.model.AddEditNoteCategoryModel
import com.yotfr.sunmoon.presentation.utils.placeCursorToEnd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomSheetAddEditNoteFragment : BottomSheetDialogFragment() {

    companion object {
        const val WITHOUT_NOTE_ID = -1L
        const val WITHOUT_CATEGORY_ID = -1L
        const val WITHOUT_CATEGORY_DESCRIPTION = ""
        const val WITHOUT_CATEGORY_POP_UP_ID = -2L
    }

    private val viewModel by viewModels<BottomSheetAddEditNoteViewModel>()

    private lateinit var binding: FragmentBottomSheetAddEditNoteBinding

    private lateinit var popUpMenuAdapter: PopUpMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //request focus on textFields
        if (binding.etNoteTitle.editText?.text?.isEmpty() == true) {
            binding.etNoteTitle.editText?.isFocusableInTouchMode = true
            binding.etNoteTitle.editText?.requestFocus()
        } else {
            binding.etNoteDescription.editText?.isFocusableInTouchMode = true
            binding.etNoteTitle.editText?.clearFocus()
            binding.etNoteDescription.editText?.requestFocus()
        }

        //initPopUpAdapter for listPopUpWindow menu
        popUpMenuAdapter = PopUpMenuAdapter()
        popUpMenuAdapter.attachDelegate(object : PopUpDelegate {
            override fun addCategoryPressed() {
                navigateToAddCategoryDialog()
            }
        })

        //show listPopUpWindow
        binding.fragmentAddEditNoteBtnAddCategory.setOnClickListener {
            val listPopupWindow = ListPopupWindow(
                requireContext(),
                null,
                com.google.android.material.R.attr.listPopupWindowStyle
            )
            listPopupWindow.anchorView = binding.popUpAnchor
            listPopupWindow.setAdapter(popUpMenuAdapter)
            listPopupWindow.setOnItemClickListener { _, view, _, id ->
                when (id) {
                    PopUpMenuAdapter.FOOTER_ID -> {
                        navigateToAddCategoryDialog()
                    }
                    else -> {
                        val category = view.findViewById<View>(R.id.tv_pop_up_menu_item)
                            .tag as AddEditNoteCategoryModel
                        if (category.id == WITHOUT_CATEGORY_POP_UP_ID) {
                            viewModel.onEvent(AddEditNoteEvent.ClearSelectedCategory)
                        } else {
                            viewModel.onEvent(
                                AddEditNoteEvent.ChangeSelectedCategory(
                                    newSelectedCategory = category
                                )
                            )
                        }
                    }
                }
                listPopupWindow.dismiss()
            }
            listPopupWindow.show()
        }

        //collect note uiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addEditNoteUiState.collect { state ->
                    state.let {
                        binding.apply {
                            //TODO
                            etNoteTitle.editText?.setText(it.title)
                            etNoteTitle.editText?.placeCursorToEnd()
                            etNoteDescription.editText?.setText(it.text)
                            etNoteDescription.editText?.placeCursorToEnd()
                        }
                    }
                }
            }
        }

        //collect category uiState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addEditNoteCategoryUiState.collect { categoryState ->
                    categoryState?.let { categories ->
                        val headerCategory = AddEditNoteCategoryModel(
                            WITHOUT_CATEGORY_POP_UP_ID,
                            categoryDescription = getString(R.string.without_category),
                            isVisible = true,
                            notesCount = null
                        )
                        if (!categories.contains(headerCategory)) {
                            categories.add(0, headerCategory)
                        }
                        popUpMenuAdapter.categories = categories
                    }
                }
            }
        }

        //collect uiEvents
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is AddEditNoteUiEvent.PopBackStack -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }

        //collect selectedCategory
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateSelectedCategory.collect { selectedCategory ->
                    binding.fragmentAddEditNoteBtnAddCategory.text =
                        selectedCategory?.categoryDescription
                            ?: getString(R.string.without_category)
                }
            }
        }

        //save note
        binding.btnAddNote.setOnClickListener {
            viewModel.onEvent(
                AddEditNoteEvent.SaveNotePressed(
                    binding.etNoteTitle.editText?.text.toString(),
                    binding.etNoteDescription.editText?.text.toString()
                )
            )
        }

        //enable/disable save note button and save title text field text
        binding.etNoteTitle.editText?.doOnTextChanged { text, _, _, _ ->
            binding.btnAddNote.isEnabled = !text.isNullOrEmpty()
            viewModel.onEvent(
                AddEditNoteEvent.SaveNewNoteTitle(
                    newTitle = text.toString()
                )
            )
        }

        //save description text field text
        binding.etNoteDescription.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(
                AddEditNoteEvent.SaveNewNoteText(
                    newText = text.toString()
                )
            )
        }
    }

    private fun navigateToAddCategoryDialog() {
        val direction = NoteRootFragmentDirections.actionGlobalAddCategoryDialogFragment(
            AddCategoryDialogFragment.WITHOUT_CATEGORY_ID,
            AddCategoryDialogFragment.WITHOUT_CATEGORY_TEXT
        )
        findNavController().navigate(direction)
    }
}