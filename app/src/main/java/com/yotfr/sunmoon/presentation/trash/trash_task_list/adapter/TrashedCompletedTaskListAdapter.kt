package com.yotfr.sunmoon.presentation.trash.trash_task_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemTrashedCompletedTaskBinding
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedTaskListModel

interface TrashedCompletedTaskListDelegate {
    fun taskItemPressed(taskId: Long)
}

class TrashedCompletedTaskListAdapter(
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

class TrashedCompletedTaskAdapter :
    RecyclerView.Adapter<TrashedCompletedTaskAdapter.CompletedTaskViewHolder>() {

    private var delegate: TrashedCompletedTaskListDelegate? = null

    fun attachDelegate(delegate: TrashedCompletedTaskListDelegate) {
        this.delegate = delegate
    }

    var tasks: List<TrashedTaskListModel> = emptyList()
        set(newValue) {
            val diffCallback = TrashedCompletedTaskListAdapter(field, newValue)
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
            ItemTrashedCompletedTaskBinding.inflate(
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
        private val binding: ItemTrashedCompletedTaskBinding,
        private val delegate: TrashedCompletedTaskListDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TrashedTaskListModel) {
            binding.apply {
                itemTrashedCompletedTaskTaskProgress.progress = task.completionProgress
                itemTrashedCompletedTaskTvScheduledTime.text = task.scheduledTimeString
                itemTrashedCompletedTaskTvTaskDescription.text = task.taskDescription
                itemTrashedCompletedTaskCb.isChecked = task.completionStatus
                itemTrashedCompletedTaskTvScheduledTime.isVisible = !task.isAddTimeButtonVisible
                itemTrashedCompletedTaskTvSetTime.isVisible = task.isAddTimeButtonVisible

                itemTrashedCompletedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskItemPressed(
                       taskId =  task.taskId
                    )
                }
            }
        }
    }
}