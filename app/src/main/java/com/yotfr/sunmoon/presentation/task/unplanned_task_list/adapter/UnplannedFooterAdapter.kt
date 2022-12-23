package com.yotfr.sunmoon.presentation.task.unplanned_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemUnplannedTaskFooterBinding
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedFooterModel

class UnplannedFooterAdapter :
    RecyclerView.Adapter<UnplannedFooterAdapter.UnplannedFooterViewHolder>() {

    var footerState: UnplannedFooterModel = UnplannedFooterModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_unplanned_task_footer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnplannedFooterViewHolder {
        return UnplannedFooterViewHolder(
            ItemUnplannedTaskFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UnplannedFooterViewHolder, position: Int) {
        holder.bind(footerState)
    }

    class UnplannedFooterViewHolder(
        private val binding: ItemUnplannedTaskFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: UnplannedFooterModel) {
            binding.scheduledFooter.visibility = if (
                headerState.isVisible
            ) View.VISIBLE else View.GONE
        }
    }
}
