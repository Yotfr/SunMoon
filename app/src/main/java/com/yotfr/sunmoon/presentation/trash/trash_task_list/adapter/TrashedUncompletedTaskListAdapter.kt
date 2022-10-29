package com.yotfr.sunmoon.presentation.trash.trash_task_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemTrashedTaskBinding
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedTaskListModel


interface UncompletedTrashTaskDelegate {
    fun taskItemPressed(taskId: Long)
}

class TrashedUncompletedTaskDiffCallback(
    private val oldList: List<TrashedTaskListModel>,
    private val newList: List<TrashedTaskListModel>
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

class TrashedUncompletedTaskListAdapter : RecyclerView.Adapter<TrashedUncompletedTaskListAdapter.TrashedTaskViewHolder>() {

    private var delegate: UncompletedTrashTaskDelegate? = null

    fun attachDelegate(delegate: UncompletedTrashTaskDelegate) {
        this.delegate = delegate
    }

    var deletedTasks: List<TrashedTaskListModel> = emptyList()
        set(newValue) {
            val diffCallback = TrashedUncompletedTaskDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashedTaskViewHolder {
        return TrashedTaskViewHolder(
            ItemTrashedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: TrashedTaskViewHolder, position: Int) {
        holder.bind(deletedTasks[position])
    }

    override fun getItemCount(): Int = deletedTasks.size


    override fun getItemViewType(position: Int): Int {
        return R.layout.item_trashed_task
    }


    class TrashedTaskViewHolder(
        private val binding: ItemTrashedTaskBinding,
        private val delegate: UncompletedTrashTaskDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TrashedTaskListModel) {
            binding.apply {
                itemTrashedTaskTvScheduledTime.text = task.scheduledTimeString
                itemTrashedTaskTvTaskDescription.text = task.taskDescription
                itemTrashedTaskCb.isChecked = task.completionStatus
                itemTrashedTaskTaskProgress.progress = task.completionProgress
                itemTrashedTaskTvScheduledTime.isVisible = !task.isAddTimeButtonVisible
                itemTrashedTaskTvSetTime.isVisible = task.isAddTimeButtonVisible

                itemTrashedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskItemPressed(
                        taskId = task.taskId
                    )
                }
            }
        }
    }
}
