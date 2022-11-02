package com.yotfr.sunmoon.presentation.task.outdated_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemOutdatedTaskFooterBinding
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedFooterModel

class OutdatedFooterAdapter :
    RecyclerView.Adapter<OutdatedFooterAdapter.OutdatedFooterViewHolder>() {

    var footerState: OutdatedFooterModel = OutdatedFooterModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_outdated_task_footer
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutdatedFooterViewHolder {
        return OutdatedFooterViewHolder(
            ItemOutdatedTaskFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OutdatedFooterViewHolder, position: Int) {
        holder.bind(footerState)
    }

    class OutdatedFooterViewHolder(
        private val binding: ItemOutdatedTaskFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: OutdatedFooterModel) {
            binding.scheduledFooter.visibility = if (
                headerState.isVisible
            ) View.VISIBLE else View.GONE

        }
    }
}