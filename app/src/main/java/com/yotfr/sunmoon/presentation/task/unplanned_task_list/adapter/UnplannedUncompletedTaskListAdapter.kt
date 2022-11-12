package com.yotfr.sunmoon.presentation.task.unplanned_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemUnplannedTaskBinding
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListModel

interface UnplannedUncompletedTaskDelegate {
    fun taskPressed(taskId: Long, transitionView: View)
    fun taskCheckBoxPressed(task: UnplannedTaskListModel)
    fun scheduleTaskPressed(task: UnplannedTaskListModel)
    fun taskStarPressed(task: UnplannedTaskListModel)
}

class UnplannedUncompletedTaskDiffCallback : DiffUtil.ItemCallback<UnplannedTaskListModel>() {
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

class UnplannedUncompletedTaskListAdapter :
    ListAdapter<UnplannedTaskListModel,
            UnplannedUncompletedTaskListAdapter.UnplannedTaskViewHolder>(
        UnplannedUncompletedTaskDiffCallback()
    ) {

    private var delegate: UnplannedUncompletedTaskDelegate? = null


    fun attachDelegate(delegate: UnplannedUncompletedTaskDelegate) {
        this.delegate = delegate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnplannedTaskViewHolder {
        return UnplannedTaskViewHolder(
            ItemUnplannedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: UnplannedTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_scheduled_task
    }

    inner class UnplannedTaskViewHolder(
        private val binding: ItemUnplannedTaskBinding,
        private val delegate: UnplannedUncompletedTaskDelegate?,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: UnplannedTaskListModel) {
            binding.apply {
                itemUnplannedTaskTaskProgress.progress = task.completionProgress
                itemUnplannedTaskTvTaskDescription.text = task.taskDescription
                itemUnplannedTaskCb.isChecked = task.isCompleted
                itemImportanceTaskCb.isChecked = task.importance
                ViewCompat.setTransitionName(itemUnplannedTaskCard, "task${task.taskId}")

                itemUnplannedTaskCb.setOnClickListener {
                    delegate?.taskCheckBoxPressed(task)
                }
                itemUnplannedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = task.taskId,
                        transitionView = itemUnplannedTaskCard
                    )
                }
                itemImportanceTaskCb.setOnClickListener {
                    delegate?.taskStarPressed(
                        task = task
                    )
                }
                itemUnplannedTaskSetDate.setOnClickListener {
                    delegate?.scheduleTaskPressed(
                        task = task
                    )
                }
            }
        }
    }
}