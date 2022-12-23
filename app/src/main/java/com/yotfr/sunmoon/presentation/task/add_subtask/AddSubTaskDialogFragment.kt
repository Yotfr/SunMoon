package com.yotfr.sunmoon.presentation.task.add_subtask

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.DialogFragmentAddSubtaskBinding
import com.yotfr.sunmoon.presentation.task.add_subtask.event.AddSubTaskDialogEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSubTaskDialogFragment : DialogFragment() {

    private val viewModel by viewModels<AddSubTaskDialogViewModel>()

    private var _binding: DialogFragmentAddSubtaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.CustomAlertDialog)
            _binding = DialogFragmentAddSubtaskBinding.inflate(layoutInflater)
            binding.btnSubTaskSave.setOnClickListener {
                viewModel.onEvent(
                    AddSubTaskDialogEvent.AddSubTask(
                        subTaskText = binding.etDialogSubtaskText.editText?.text.toString()
                    )
                )
                dialog?.dismiss()
            }
            binding.btnSubTaskCancel.setOnClickListener {
                dialog?.dismiss()
            }
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
