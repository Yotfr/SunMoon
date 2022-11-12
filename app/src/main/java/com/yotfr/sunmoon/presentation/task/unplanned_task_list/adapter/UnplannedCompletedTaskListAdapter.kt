package com.yotfr.sunmoon.presentation.task.unplanned_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemUnplannedCompletedTaskBinding
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedTaskListModel

interface UnplannedCompletedTaskDelegate {
    fun taskPressed(taskId: Long, transitionView: View)
    fun taskCheckBoxPressed(task: UnplannedTaskListModel)
}

class UnplannedCompletedTaskDiffCallback(
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


class UnplannedCompletedTaskListAdapter :
    RecyclerView.Adapter<UnplannedCompletedTaskListAdapter.UnplannedCompletedTaskViewHolder>() {

    private var delegate: UnplannedCompletedTaskDelegate? = null


    fun attachDelegate(delegate: UnplannedCompletedTaskDelegate) {
        this.delegate = delegate
    }

    var tasks: List<UnplannedTaskListModel> = emptyList()
        set(newValue) {
            val diffCallback = UnplannedCompletedTaskDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UnplannedCompletedTaskViewHolder {
        return UnplannedCompletedTaskViewHolder(
            ItemUnplannedCompletedTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: UnplannedCompletedTaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size


    override fun getItemViewType(position: Int): Int {
        return  R.layout.item_unplanned_completed_task
    }

    class UnplannedCompletedTaskViewHolder(
        private val binding: ItemUnplannedCompletedTaskBinding,
        private val delegate: UnplannedCompletedTaskDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: UnplannedTaskListModel) {
            binding.apply {
                itemUnplannedCompletedTaskTaskProgress.progress = task.completionProgress
                itemUnplannedCompletedTaskTvTaskDescription.text = task.taskDescription
                itemUnplannedCompletedTaskCb.isChecked = task.isCompleted
                ViewCompat.setTransitionName(itemUnplannedCompletedTaskCard, "task${task.taskId}")

                itemUnplannedCompletedTaskCb.setOnClickListener {
                    delegate?.taskCheckBoxPressed(task)
                }
                itemUnplannedCompletedTaskTvTaskDescription.setOnClickListener {
                    delegate?.taskPressed(
                        taskId = task.taskId,
                        transitionView = itemUnplannedCompletedTaskCard
                    )
                }
            }
        }
    }
}