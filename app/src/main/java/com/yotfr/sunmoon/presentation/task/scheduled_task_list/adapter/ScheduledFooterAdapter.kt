package com.yotfr.sunmoon.presentation.task.scheduled_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemScheduledTaskFooterBinding
import com.yotfr.sunmoon.presentation.task.scheduled_task_list.model.ScheduledFooterModel

class ScheduledFooterAdapter :
    RecyclerView.Adapter<ScheduledFooterAdapter.ScheduledFooterViewHolder>() {

    var footerState: ScheduledFooterModel = ScheduledFooterModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_scheduled_task_footer
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduledFooterViewHolder {
        return ScheduledFooterViewHolder(
            ItemScheduledTaskFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ScheduledFooterViewHolder, position: Int) {
        holder.bind(footerState)
    }

    class ScheduledFooterViewHolder(
        private val binding: ItemScheduledTaskFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: ScheduledFooterModel) {
            binding.scheduledFooter.visibility = if (
                headerState.isVisible
            ) View.VISIBLE else View.GONE

        }
    }
}