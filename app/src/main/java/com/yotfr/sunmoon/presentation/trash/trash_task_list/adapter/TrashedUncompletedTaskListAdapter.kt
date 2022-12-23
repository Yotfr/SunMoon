package com.yotfr.sunmoon.presentation.trash.trash_task_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemTrashedTaskBinding
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedTaskListModel

class TrashedUncompletedTaskDiffCallback : DiffUtil.ItemCallback<TrashedTaskListModel>() {

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

class TrashedUncompletedTaskListAdapter : ListAdapter<TrashedTaskListModel, TrashedUncompletedTaskListAdapter.TrashedTaskViewHolder>(
    TrashedUncompletedTaskDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashedTaskViewHolder {
        return TrashedTaskViewHolder(
            ItemTrashedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrashedTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_trashed_task
    }

    class TrashedTaskViewHolder(
        private val binding: ItemTrashedTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TrashedTaskListModel) {
            binding.apply {
                itemTrashedTaskTvScheduledTime.text = task.scheduledTimeString
                itemTrashedTaskTvTaskDescription.text = task.taskDescription
                itemTrashedTaskCb.isChecked = task.completionStatus
                itemTrashedTaskTaskProgress.progress = task.completionProgress
                itemTrashedTaskTvScheduledTime.isVisible = !task.isAddTimeButtonVisible
                itemTrashedTaskTvSetTime.isVisible = task.isAddTimeButtonVisible
            }
        }
    }
}
