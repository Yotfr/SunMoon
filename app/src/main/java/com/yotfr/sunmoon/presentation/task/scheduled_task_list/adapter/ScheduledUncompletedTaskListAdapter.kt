package com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemScheduledTaskBinding
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledTaskListModel
import kotlin.IllegalArgumentException

interface ScheduledUncompletedTaskListDelegate {
    fun taskPressed(taskId: Long, transitionView:View)
    fun taskCheckBoxPressed(task: ScheduledTaskListModel)
    fun taskTimePressed(task: ScheduledTaskListModel)
    fun taskStarPressed(task: ScheduledTaskListModel)
}

class ScheduledUncompletedTaskDiffCallback(
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


class ScheduledUncompletedTaskListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var delegate: ScheduledUncompletedTaskListDelegate? = null


    fun attachDelegate(delegate: ScheduledUncompletedTaskListDelegate) {
        this.delegate = delegate
    }

    var tasks: List<ScheduledTaskListModel> = emptyList()
        set(newValue) {
            val diffCallback = ScheduledUncompletedTaskDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScheduledUncompletedTaskListViewHolder -> {
                holder.bind(tasks[position])
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return R.layout.item_scheduled_task
    }

    override fun getItemCount(): Int = tasks.size


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