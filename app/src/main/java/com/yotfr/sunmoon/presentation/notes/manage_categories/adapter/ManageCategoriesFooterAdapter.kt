package com.yotfr.sunmoon.presentation.notes.manage_categories.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemManageCategoriesFooterBinding
import com.yotfr.sunmoon.presentation.notes.manage_categories.model.ManageCategoriesFooterModel

class ManageCategoriesFooterAdapter :
    RecyclerView.Adapter<ManageCategoriesFooterAdapter.ManageCategoriesFooterViewHolder>() {

    var footerState: ManageCategoriesFooterModel = ManageCategoriesFooterModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_note_list_footer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageCategoriesFooterViewHolder {
        return ManageCategoriesFooterViewHolder(
            ItemManageCategoriesFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ManageCategoriesFooterViewHolder, position: Int) {
        holder.bind(footerState)
    }

    class ManageCategoriesFooterViewHolder(
        private val binding: ItemManageCategoriesFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: ManageCategoriesFooterModel) {
            binding.scheduledFooter.visibility = if (
                headerState.isVisible
            ) View.VISIBLE else View.GONE
        }
    }
}
