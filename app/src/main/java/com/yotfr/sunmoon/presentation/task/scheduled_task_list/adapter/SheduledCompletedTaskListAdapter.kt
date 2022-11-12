package com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemScheduledCompletedTaskBinding
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel

interface ScheduledCompletedTaskListDelegate {
    fun taskPressed(taskId: Long, transitionView: View)
    fun taskCheckBoxPressed(task: ScheduledTaskListModel)
}

class ScheduledCompletedTaskDiffCallback : DiffUtil.ItemCallback<ScheduledTaskListModel>() {
    override fun areItemsTheSame(
        oldItem: ScheduledTaskListModel,
        newItem: ScheduledTaskListModel
    ): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(
        oldItem: ScheduledTaskListModel,
        newItem: ScheduledTaskListModel
    ): Boolean {
        return oldItem == newItem
    }
}

class ScheduledCompletedTaskAdapter :
    ListAdapter<ScheduledTaskListModel, ScheduledCompletedTaskAdapter.CompletedTaskViewHolder>(
        ScheduledCompletedTaskDiffCallback()
    ) {

    private var delegate: ScheduledCompletedTaskListDelegate? = null

    fun attachDelegate(delegate: ScheduledCompletedTaskListDelegate) {
        this.delegate = delegate
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_scheduled_completed_task
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskViewHolder {
        return CompletedTaskViewHolder(
            ItemScheduledCompletedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }


    override fun onBindViewHolder(holder: CompletedTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class CompletedTaskViewHolder(
        private val binding: ItemScheduledCompletedTaskBinding,
        private val delegate: ScheduledCompletedTaskListDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: ScheduledTaskListModel) {
            binding.apply {
                itemScheduledCompletedTaskTaskProgress.progress = task.completionProgress
                itemScheduledCompletedTaskTvScheduledTime.text = task.scheduledFormattedTime
                itemScheduledCompletedTaskTvScheduledTime.isVisible = !task.isAddTimeButtonVisible
                itemScheduledCompletedTaskTvTaskDescription.text = task.taskDescription
                itemScheduledCompletedTaskCb.isChecked = task.isCompleted
                itemScheduledCompletedTaskTvSetTime.isVisible = task.isAddTimeButtonVisible
                ViewCompat.setTransitionName(itemScheduledCompletedTaskCard, "task${task.taskId}")

                itemScheduledCompletedTaskCb.setOnClickListener {
                    delegate?.taskCheckBoxPressed(
                        task = task
                    )
                }

                itemScheduledCompletedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = task.taskId,
                        transitionView = itemScheduledCompletedTaskCard
                    )
                }

            }
        }
    }
}