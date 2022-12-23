package com.yotfr.sunmoon.presentation.notes.note_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemNoteListFooterBinding
import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListFooterModel

class NoteListFooterAdapter :
    RecyclerView.Adapter<NoteListFooterAdapter.NoteListFooterViewHolder>() {

    var footerState: NoteListFooterModel = NoteListFooterModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_note_list_footer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListFooterViewHolder {
        return NoteListFooterViewHolder(
            ItemNoteListFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteListFooterViewHolder, position: Int) {
        holder.bind(footerState)
    }

    class NoteListFooterViewHolder(
        private val binding: ItemNoteListFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: NoteListFooterModel) {
            binding.scheduledFooter.visibility = if (
                headerState.isVisible
            ) View.VISIBLE else View.GONE
        }
    }
}
