package com.yotfr.sunmoon.presentation.task.unplanned_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemUnplannedCompletedTaskBinding
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListModel

interface UnplannedCompletedTaskDelegate {
    fun taskPressed(taskId: Long, transitionView: View)
    fun taskCheckBoxPressed(task: UnplannedTaskListModel)
}

class UnplannedCompletedTaskDiffCallback: DiffUtil.ItemCallback<UnplannedTaskListModel>() {

    override fun areItemsTheSame(
        oldItem: UnplannedTaskListModel,
        newItem: UnplannedTaskListModel
    ): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(
        oldItem: UnplannedTaskListModel,
        newItem: UnplannedTaskListModel
    ): Boolean {
        return oldItem == newItem

    }
}


class UnplannedCompletedTaskListAdapter :
    ListAdapter<UnplannedTaskListModel,
            UnplannedCompletedTaskListAdapter.UnplannedCompletedTaskViewHolder>(
        UnplannedCompletedTaskDiffCallback()
            ) {

    private var delegate: UnplannedCompletedTaskDelegate? = null


    fun attachDelegate(delegate: UnplannedCompletedTaskDelegate) {
        this.delegate = delegate
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UnplannedCompletedTaskViewHolder {
        return UnplannedCompletedTaskViewHolder(
            ItemUnplannedCompletedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: UnplannedCompletedTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return  R.layout.item_unplanned_completed_task
    }

    class UnplannedCompletedTaskViewHolder(
        private val binding: ItemUnplannedCompletedTaskBinding,
        private val delegate: UnplannedCompletedTaskDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: UnplannedTaskListModel) {
            binding.apply {
                itemUnplannedCompletedTaskTaskProgress.progress = task.completionProgress
                itemUnplannedCompletedTaskTvTaskDescription.text = task.taskDescription
                itemUnplannedCompletedTaskCb.isChecked = task.isCompleted
                ViewCompat.setTransitionName(itemUnplannedCompletedTaskCard, "task${task.taskId}")

                itemUnplannedCompletedTaskCb.setOnClickListener {
                    delegate?.taskCheckBoxPressed(task)
                }
                itemUnplannedCompletedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = task.taskId,
                        transitionView = itemUnplannedCompletedTaskCard
                    )
                }
            }
        }
    }
}