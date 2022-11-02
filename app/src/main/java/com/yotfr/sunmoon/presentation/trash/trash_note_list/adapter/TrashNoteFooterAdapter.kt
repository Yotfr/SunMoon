package com.yotfr.sunmoon.presentation.trash.trash_note_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemTrashNoteFooterBinding
import com.yotfr.sunmoon.presentation.trash.trash_note_list.model.TrashNoteFooterModel

class TrashNoteFooterAdapter :
    RecyclerView.Adapter<TrashNoteFooterAdapter.TrashNoteListFooterViewHolder>() {

    var footerState: TrashNoteFooterModel = TrashNoteFooterModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_trash_note_footer
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashNoteListFooterViewHolder {
        return TrashNoteListFooterViewHolder(
            ItemTrashNoteFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrashNoteListFooterViewHolder, position: Int) {
        holder.bind(footerState)
    }

    class TrashNoteListFooterViewHolder(
        private val binding: ItemTrashNoteFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: TrashNoteFooterModel) {
            binding.trashNoteFooter.visibility = if (
                headerState.isVisible
            ) View.VISIBLE else View.GONE

        }
    }
}