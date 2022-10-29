package com.yotfr.sunmoon.presentation.task.task_details.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemAddSubtaskBinding
import com.yotfr.sunmoon.databinding.ItemSubtaskBinding
import com.yotfr.sunmoon.presentation.task.task_details.model.SubTaskModel

interface SubTaskDelegate {
    fun subTaskCheckBoxClicked(subTask: SubTaskModel)
    fun addSubTaskPressed()
    fun subTaskTextChanged(subTask: SubTaskModel, newText: String)
    fun removeEmptySubTask(subTask: SubTaskModel)
}

class SubTaskDiffCallback(
    private val oldList: List<SubTaskModel>,
    private val newList: List<SubTaskModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].taskId == newList[newItemPosition].taskId
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

open class SubTaskAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var delegate: SubTaskDelegate? = null

    fun attachDelegate(delegate: SubTaskDelegate) {
        this.delegate = delegate
    }



    var subTasks: List<SubTaskModel> = emptyList()
        set(newValue) {
            val diffCallback = SubTaskDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_subtask -> {
                SubTaskViewHolder(
                    ItemSubtaskBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), delegate, EditTextListener()
                )
            }
            R.layout.item_add_subtask -> {
                FooterViewHolder(
                    ItemAddSubtaskBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), delegate
                )
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SubTaskViewHolder -> {
                holder.bind(subTasks[position])
                holder.listener.subTask = subTasks[position]
            }
            is FooterViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == subTasks.size) R.layout.item_add_subtask
        else R.layout.item_subtask
    }


    override fun getItemCount(): Int {
        if (subTasks.isEmpty()) {
            return 1
        }
        return subTasks.size + 1
    }

    class SubTaskViewHolder(
        private val binding: ItemSubtaskBinding,
        private val delegate: SubTaskDelegate?,
        textWatcher: EditTextListener
    ) : RecyclerView.ViewHolder(binding.root) {
        val listener = textWatcher
        fun bind(subTask: SubTaskModel) {
            binding.apply {
                fragmentItemSubtaskCb.isChecked = subTask.completionStatus ?: false
                fragmentItemSubtaskCb.setOnClickListener {
                    delegate?.subTaskCheckBoxClicked(subTask)
                }
                itemNestedTitle.addTextChangedListener(listener)
                itemNestedTitle.setText(subTask.subTaskDescription)

                itemNestedTitle.setOnFocusChangeListener { _, isFocused ->
                    if(!isFocused) {
                        fragmentItemSubtaskCb.isEnabled = true
                        if (listener.changedText.isEmpty()) {
                            delegate?.removeEmptySubTask(subTask)
                        } else {
                            delegate?.subTaskTextChanged(subTask, listener.changedText)
                        }
                        listener.changedText = ""
                    }else {
                        fragmentItemSubtaskCb.isEnabled = false
                    }
                }
            }
        }
    }

    class FooterViewHolder(
        private val binding: ItemAddSubtaskBinding,
        private val delegate: SubTaskDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.btnAddSubTask.setOnClickListener {
                delegate?.addSubTaskPressed()
            }
        }
    }

    class EditTextListener : TextWatcher {

        var changedText = ""

        var subTask: SubTaskModel? = null

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            changedText = text.toString()
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }


}