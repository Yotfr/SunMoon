package com.yotfr.sunmoon.presentation.trash.trash_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemTrashTaskFooterBinding
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashTaskFooterModel

class TrashTaskFooterAdapter :
    RecyclerView.Adapter<TrashTaskFooterAdapter.TrashTaskFooterViewHolder>() {

    var footerState: TrashTaskFooterModel = TrashTaskFooterModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_trash_task_footer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashTaskFooterViewHolder {
        return TrashTaskFooterViewHolder(
            ItemTrashTaskFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrashTaskFooterViewHolder, position: Int) {
        holder.bind(footerState)
    }

    class TrashTaskFooterViewHolder(
        private val binding: ItemTrashTaskFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: TrashTaskFooterModel) {
            binding.scheduledFooter.visibility = if (
                headerState.isVisible
            ) View.VISIBLE else View.GONE
        }
    }
}
