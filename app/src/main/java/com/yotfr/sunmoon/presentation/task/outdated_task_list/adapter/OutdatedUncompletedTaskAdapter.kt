package com.yotfr.sunmoon.presentation.task.outdated_task_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.databinding.ItemOutdatedTaskBinding
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedTaskListModel


interface OutdatedUncompletedTaskDelegate{
    fun taskPressed(taskId:Long)
    fun schedulePressed(task:OutdatedTaskListModel)
}

class OutdatedUncompletedTaskDiffCallback(
    private val oldList:List<OutdatedTaskListModel>,
    private val newList:List<OutdatedTaskListModel>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].taskId == newList[newItemPosition].taskId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

class OutdatedUncompletedTaskAdapter:RecyclerView.Adapter<OutdatedUncompletedTaskAdapter.OutdatedTaskViewHolder>() {

    private var delegate: OutdatedUncompletedTaskDelegate? = null


    fun attachDelegate(delegate: OutdatedUncompletedTaskDelegate) {
        this.delegate = delegate
    }

    var outdatedTasks: List<OutdatedTaskListModel> = emptyList()
        set(newValue) {
            val diffCallback = OutdatedUncompletedTaskDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutdatedTaskViewHolder {
        return OutdatedTaskViewHolder(
            ItemOutdatedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),delegate
        )
    }

    override fun onBindViewHolder(holder: OutdatedTaskViewHolder, position: Int) {
        holder.bind(outdatedTasks[position])
    }

    override fun getItemCount(): Int = outdatedTasks.size

    class OutdatedTaskViewHolder(
        private val binding:ItemOutdatedTaskBinding,
        private val delegate: OutdatedUncompletedTaskDelegate?
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(outdatedTask:OutdatedTaskListModel){
            binding.apply {

                itemOutdatedTaskTvScheduledTime.text = outdatedTask.scheduledFormattedTime
                itemOutdatedTaskTvScheduledTime.isVisible = outdatedTask.isAddTimeButtonVisible
                itemOutdatedTaskTvTaskDescription.text = outdatedTask.taskDescription
                itemOutdatedTaskCb.isChecked = outdatedTask.isCompleted
                itemOutdatedTaskTaskProgress.progress = outdatedTask.completionProgress
                itemOutdatedTaskTvOverdue.text = outdatedTask.formattedOverDueTime

                itemOutdatedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                       taskId =  outdatedTask.taskId
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