package com.yotfr.sunmoon.presentation.task.add_subtask

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.DialogFragmentAddSubtaskBinding
import com.yotfr.sunmoon.presentation.task.add_subtask.event.AddSubTaskDialogEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSubTaskDialogFragment : DialogFragment() {

    private val viewModel by viewModels<AddSubTaskDialogViewModel>()
    private lateinit var binding: DialogFragmentAddSubtaskBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            binding = DialogFragmentAddSubtaskBinding.inflate(LayoutInflater.from(context))

            builder.setView(binding.root)
                .setPositiveButton(R.string.save) { _, _ ->
                    viewModel.onEvent(
                        AddSubTaskDialogEvent.AddSubTask(
                            subTaskText = binding.etDialogSubtaskText.editText?.text.toString()
                        )
                    )
                }
                .setNegativeButton(R.string.cancel, null)
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}