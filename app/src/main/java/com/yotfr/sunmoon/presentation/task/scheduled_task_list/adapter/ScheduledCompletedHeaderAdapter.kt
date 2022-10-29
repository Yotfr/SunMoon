package com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemScheduledCompletedTaskHeaderBinding
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledCompletedHeaderStateModel

interface ScheduledCompletedHeaderDelegate {
    fun hideCompleted()
}

class ScheduledCompletedHeaderAdapter : RecyclerView.Adapter<ScheduledCompletedHeaderAdapter.CompletedTaskHeaderViewHolder>() {

    private var delegate: ScheduledCompletedHeaderDelegate? = null

    fun attachDelegate(delegate: ScheduledCompletedHeaderDelegate) {
        this.delegate = delegate
    }

    var headerState: ScheduledCompletedHeaderStateModel = ScheduledCompletedHeaderStateModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_scheduled_completed_task_header
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskHeaderViewHolder {
        return CompletedTaskHeaderViewHolder(
            ItemScheduledCompletedTaskHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: CompletedTaskHeaderViewHolder, position: Int) {
       holder.bind(headerState)
    }

    class CompletedTaskHeaderViewHolder(
        private val binding: ItemScheduledCompletedTaskHeaderBinding,
        private val delegate:ScheduledCompletedHeaderDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState:ScheduledCompletedHeaderStateModel) {
            binding.llScheduledCompletedHeader.visibility = if (headerState.isVisible) View.VISIBLE
            else View.GONE
            binding.btnCompletedHeaderExpand.isChecked = headerState.isExpanded
            binding.btnCompletedHeaderExpand.setOnClickListener {
                delegate?.hideCompleted()
            }
        }
    }
}