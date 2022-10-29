package com.yotfr.sunmoon.presentation.task.unplanned_task_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemUnplannedTaskBinding
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListModel

interface UnplannedUncompletedTaskDelegate {
    fun taskPressed(taskId: Long)
    fun taskCheckBoxPressed(task: UnplannedTaskListModel)
    fun scheduleTaskPressed(task: UnplannedTaskListModel)
    fun taskStarPressed(task: UnplannedTaskListModel)
}

class UnplannedUncompletedTaskDiffCallback(
    private val oldList: List<UnplannedTaskListModel>,
    private val newList: List<UnplannedTaskListModel>
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

class UnplannedUncompletedTaskListAdapter :
    RecyclerView.Adapter<UnplannedUncompletedTaskListAdapter.UnplannedTaskViewHolder>() {

    private var delegate: UnplannedUncompletedTaskDelegate? = null


    fun attachDelegate(delegate: UnplannedUncompletedTaskDelegate) {
        this.delegate = delegate
    }

    var tasks: List<UnplannedTaskListModel> = emptyList()
        set(newValue) {
            val diffCallback = UnplannedUncompletedTaskDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
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
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size


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

                itemUnplannedTaskCb.setOnClickListener {
                    delegate?.taskCheckBoxPressed(task)
                }
                itemUnplannedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = task.taskId
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