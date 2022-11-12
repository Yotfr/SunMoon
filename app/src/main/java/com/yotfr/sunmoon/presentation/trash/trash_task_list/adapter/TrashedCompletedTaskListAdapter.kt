package com.yotfr.sunmoon.presentation.trash.trash_task_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemTrashedCompletedTaskBinding
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedTaskListModel

class TrashedCompletedTaskListAdapter : DiffUtil.ItemCallback<TrashedTaskListModel>() {

    override fun areItemsTheSame(
        oldItem: TrashedTaskListModel,
        newItem: TrashedTaskListModel
    ): Boolean {
        return oldItem.taskId == newItem.taskId

    }

    override fun areContentsTheSame(
        oldItem: TrashedTaskListModel,
        newItem: TrashedTaskListModel
    ): Boolean {
        return oldItem == newItem

    }
}

class TrashedCompletedTaskAdapter :
    ListAdapter<TrashedTaskListModel, TrashedCompletedTaskAdapter.CompletedTaskViewHolder>(
        TrashedCompletedTaskListAdapter()
    ) {

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_scheduled_completed_task
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskViewHolder {
        return CompletedTaskViewHolder(
            ItemTrashedCompletedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CompletedTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CompletedTaskViewHolder(
        private val binding: ItemTrashedCompletedTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TrashedTaskListModel) {
            binding.apply {
                itemTrashedCompletedTaskTaskProgress.progress = task.completionProgress
                itemTrashedCompletedTaskTvScheduledTime.text = task.scheduledTimeString
                itemTrashedCompletedTaskTvTaskDescription.text = task.taskDescription
                itemTrashedCompletedTaskCb.isChecked = task.completionStatus
                itemTrashedCompletedTaskTvScheduledTime.isVisible = !task.isAddTimeButtonVisible
                itemTrashedCompletedTaskTvSetTime.isVisible = task.isAddTimeButtonVisible
            }
        }
    }
}