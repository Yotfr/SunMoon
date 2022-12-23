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
import com.yotfr.sunmoon.databinding.ItemOutdatedTaskBinding
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListModel

interface OutdatedUncompletedTaskDelegate {
    fun taskPressed(taskId: Long, transitionView: View)
    fun schedulePressed(task: OutdatedTaskListModel)
}

class OutdatedUncompletedTaskDiffCallback : DiffUtil.ItemCallback<OutdatedTaskListModel>() {
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

class OutdatedUncompletedTaskAdapter :
    ListAdapter<OutdatedTaskListModel,
        OutdatedUncompletedTaskAdapter.OutdatedTaskViewHolder>(
        OutdatedUncompletedTaskDiffCallback()
    ) {

    private var delegate: OutdatedUncompletedTaskDelegate? = null

    fun attachDelegate(delegate: OutdatedUncompletedTaskDelegate) {
        this.delegate = delegate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutdatedTaskViewHolder {
        return OutdatedTaskViewHolder(
            ItemOutdatedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            delegate
        )
    }

    override fun onBindViewHolder(holder: OutdatedTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OutdatedTaskViewHolder(
        private val binding: ItemOutdatedTaskBinding,
        private val delegate: OutdatedUncompletedTaskDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(outdatedTask: OutdatedTaskListModel) {
            binding.apply {
                itemOutdatedTaskTvScheduledTime.text = outdatedTask.scheduledFormattedTime
                itemOutdatedTaskTvScheduledTime.isVisible = outdatedTask.isAddTimeButtonVisible
                itemOutdatedTaskTvTaskDescription.text = outdatedTask.taskDescription
                itemOutdatedTaskCb.isChecked = outdatedTask.isCompleted
                itemScheduledTaskTvSetTime.isVisible = !outdatedTask.isAddTimeButtonVisible
                itemOutdatedTaskTaskProgress.progress = outdatedTask.completionProgress
                itemOutdatedTaskTvOverdue.text =
                    itemView.context.resources.getString(
                        R.string.task_overdue,
                        outdatedTask.formattedOverDueTime
                    )
                ViewCompat.setTransitionName(itemOutdatedTaskCard, "task${outdatedTask.taskId}")

                itemOutdatedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = outdatedTask.taskId,
                        transitionView = itemOutdatedTaskCard
                    )
                }
                itemOutdatedTaskReschedule.setOnClickListener {
                    delegate?.schedulePressed(
                        task = outdatedTask
                    )
                }
            }
        }
    }
}
