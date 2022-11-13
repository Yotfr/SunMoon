package com.yotfr.sunmoon.presentation.notes.add_category

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.DialogFragmentAddCategoryBinding
import com.yotfr.sunmoon.presentation.notes.add_category.event.AddCategoryDialogEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCategoryDialogFragment : DialogFragment() {

    private val viewModel by viewModels<AddCategoryDialogViewModel>()
    private lateinit var binding: DialogFragmentAddCategoryBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            binding = DialogFragmentAddCategoryBinding.inflate(layoutInflater)
            binding.etDialogCategoryText.editText?.setText(viewModel.addCategoryDescriptionState.value)
            builder.setView(binding.root)
                .setPositiveButton(R.string.save) { _, _ ->
                    viewModel.onEvent(
                        AddCategoryDialogEvent.AddCategory(
                            binding.etDialogCategoryText.editText?.text.toString()
                        )
                    )
                }
                .setNegativeButton(R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object{
        const val WITHOUT_CATEGORY_ID = -1L
        const val WITHOUT_CATEGORY_TEXT = ""
    }

}