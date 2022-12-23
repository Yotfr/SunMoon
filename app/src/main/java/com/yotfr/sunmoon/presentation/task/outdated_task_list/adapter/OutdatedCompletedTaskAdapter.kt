package com.yotfr.sunmoon.presentation.task.outdated_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemOutdatedCompletedTaskBinding
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListModel

interface OutdatedCompletedTaskListDelegate {
    fun taskPressed(taskId: Long, transitionView: View)
    fun schedulePressed(task: OutdatedTaskListModel)
}

class OutdatedCompletedTaskDiffCallback : DiffUtil.ItemCallback<OutdatedTaskListModel>() {
    override fun areItemsTheSame(
        oldItem: OutdatedTaskListModel,
        newItem: OutdatedTaskListModel
    ): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(
        oldItem: OutdatedTaskListModel,
        newItem: OutdatedTaskListModel
    ): Boolean {
        return oldItem == newItem
    }
}

class OutdatedCompletedTaskAdapter :
    ListAdapter<OutdatedTaskListModel,
        OutdatedCompletedTaskAdapter.OutdatedCompletedTaskViewHolder>(
        OutdatedCompletedTaskDiffCallback()
    ) {

    private var delegate: OutdatedCompletedTaskListDelegate? = null

    fun attachDelegate(delegate: OutdatedCompletedTaskListDelegate) {
        this.delegate = delegate
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OutdatedCompletedTaskViewHolder {
        return OutdatedCompletedTaskViewHolder(
            ItemOutdatedCompletedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            delegate
        )
    }

    override fun onBindViewHolder(holder: OutdatedCompletedTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_scheduled_completed_task
    }

    class OutdatedCompletedTaskViewHolder(
        private val binding: ItemOutdatedCompletedTaskBinding,
        private val delegate: OutdatedCompletedTaskListDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(outdatedTask: OutdatedTaskListModel) {
            binding.apply {
                itemOutdatedCompletedTaskTaskProgress.progress = outdatedTask.completionProgress
                itemOutdatedCompletedTaskTvScheduledTime.text = outdatedTask.scheduledFormattedTime
                itemOutdatedCompletedTaskTvScheduledTime.isVisible =
                    !outdatedTask.isAddTimeButtonVisible
                itemOutdatedCompletedTaskTvTaskDescription.text = outdatedTask.taskDescription
                itemOutdatedCompletedTaskTvSetTime.isVisible = outdatedTask.isAddTimeButtonVisible
                itemOutdatedCompletedTaskCb.isChecked = outdatedTask.isCompleted
                itemOutdatedCompletedTaskTvOverdue.text =
                    itemView.context.resources.getString(
                        R.string.task_overdue,
                        outdatedTask.formattedOverDueTime
                    )
                ViewCompat.setTransitionName(
                    itemOutdatedCompletedTaskCard,
                    "task${outdatedTask.taskId}"
                )

                itemOutdatedCompletedTaskReschedule.setOnClickListener {
                    delegate?.schedulePressed(
                        task = outdatedTask
                    )
                }

                itemOutdatedCompletedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = outdatedTask.taskId,
                        transitionView = itemOutdatedCompletedTaskCard
                    )
                }
            }
        }
    }
}
