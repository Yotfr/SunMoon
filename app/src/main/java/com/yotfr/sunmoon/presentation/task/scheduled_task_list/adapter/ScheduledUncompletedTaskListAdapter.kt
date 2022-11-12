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
import com.yotfr.sunmoon.databinding.ItemScheduledTaskBinding
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel
import kotlin.IllegalArgumentException

interface ScheduledUncompletedTaskListDelegate {
    fun taskPressed(taskId: Long, transitionView: View)
    fun taskCheckBoxPressed(task: ScheduledTaskListModel)
    fun taskTimePressed(task: ScheduledTaskListModel)
    fun taskStarPressed(task: ScheduledTaskListModel)
}

class ScheduledUncompletedTaskDiffCallback: DiffUtil.ItemCallback<ScheduledTaskListModel>() {
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


class ScheduledUncompletedTaskListAdapter : ListAdapter<ScheduledTaskListModel,
        ScheduledUncompletedTaskListAdapter.ScheduledUncompletedTaskListViewHolder>(
    ScheduledUncompletedTaskDiffCallback()
        ) {

    private var delegate: ScheduledUncompletedTaskListDelegate? = null


    fun attachDelegate(delegate: ScheduledUncompletedTaskListDelegate) {
        this.delegate = delegate
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduledUncompletedTaskListViewHolder {
        return when (viewType) {
            R.layout.item_scheduled_task -> {
                ScheduledUncompletedTaskListViewHolder(
                    ItemScheduledTaskBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), delegate
                )
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: ScheduledUncompletedTaskListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    override fun getItemViewType(position: Int): Int {
        return R.layout.item_scheduled_task
    }



    class ScheduledUncompletedTaskListViewHolder(
        private val binding: ItemScheduledTaskBinding,
        private val delegate: ScheduledUncompletedTaskListDelegate?,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: ScheduledTaskListModel) {
            binding.apply {
                itemScheduledTaskTaskProgress.progress = task.completionProgress
                itemScheduledTaskTvScheduledTime.text = task.scheduledFormattedTime
                itemScheduledTaskTvScheduledTime.isVisible = !task.isAddTimeButtonVisible
                itemScheduledTaskTvTaskDescription.text = task.taskDescription
                itemScheduledTaskCb.isChecked = task.isCompleted
                itemScheduledTaskTvSetTime.isVisible = task.isAddTimeButtonVisible
                itemImportanceTaskCb.isChecked = task.importance
                ViewCompat.setTransitionName(itemScheduledTaskCard, "task${task.taskId}")

                itemScheduledTaskTvScheduledTime.setOnClickListener {
                    delegate?.taskTimePressed(
                        task = task
                    )
                }

                itemScheduledTaskCb.setOnClickListener {
                    delegate?.taskCheckBoxPressed(
                        task = task
                    )
                }

                itemImportanceTaskCb.setOnClickListener {
                    delegate?.taskStarPressed(
                        task = task
                    )
                }

                itemScheduledTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = task.taskId,
                        transitionView = itemScheduledTaskCard
                    )
                }
                itemScheduledTaskTvSetTime.setOnClickListener {
                    delegate?.taskTimePressed(
                        task = task
                    )
                }


            }
        }
    }


}