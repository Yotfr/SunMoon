package com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemScheduledCompletedTaskBinding
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel

interface ScheduledCompletedTaskListDelegate {
    fun taskPressed(taskId: Long)
    fun taskCheckBoxPressed(task: ScheduledTaskListModel)
}

class ScheduledCompletedTaskDiffCallback(
    private val oldList: List<ScheduledTaskListModel>,
    private val newList: List<ScheduledTaskListModel>
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

class ScheduledCompletedTaskAdapter :
    RecyclerView.Adapter<ScheduledCompletedTaskAdapter.CompletedTaskViewHolder>() {

    private var delegate: ScheduledCompletedTaskListDelegate? = null

    fun attachDelegate(delegate: ScheduledCompletedTaskListDelegate) {
        this.delegate = delegate
    }

    var tasks: List<ScheduledTaskListModel> = emptyList()
        set(newValue) {
            val diffCallback = ScheduledCompletedTaskDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }


    override fun getItemCount(): Int = tasks.size


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
        holder.bind(tasks[position])
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

                itemScheduledCompletedTaskCb.setOnClickListener {
                    delegate?.taskCheckBoxPressed(
                        task = task
                    )
                }

                itemScheduledCompletedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = task.taskId
                    )
                }

            }
        }
    }
}