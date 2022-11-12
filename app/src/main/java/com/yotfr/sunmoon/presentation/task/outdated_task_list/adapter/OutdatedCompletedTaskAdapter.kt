package com.yotfr.sunmoon.presentation.task.outdated_task_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemOutdatedCompletedTaskBinding
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListModel

interface OutdatedCompletedTaskListDelegate {
    fun taskPressed(taskId: Long)
    fun schedulePressed(task: OutdatedTaskListModel)
}

class OutdatedCompletedTaskDiffCallback(
    private val oldList: List<OutdatedTaskListModel>,
    private val newList: List<OutdatedTaskListModel>
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

class OutdatedCompletedTaskAdapter :
    RecyclerView.Adapter<OutdatedCompletedTaskAdapter.OutdatedCompletedTaskViewHolder>() {

    private var delegate: OutdatedCompletedTaskListDelegate? = null

    fun attachDelegate(delegate: OutdatedCompletedTaskListDelegate) {
        this.delegate = delegate
    }

    var tasks: List<OutdatedTaskListModel> = emptyList()
        set(newValue) {
            val diffCallback = OutdatedCompletedTaskDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
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
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: OutdatedCompletedTaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size


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

                itemOutdatedCompletedTaskReschedule.setOnClickListener {
                    delegate?.schedulePressed(
                        task = outdatedTask
                    )
                }

                itemOutdatedCompletedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = outdatedTask.taskId
                    )
                }

            }
        }
    }
}
