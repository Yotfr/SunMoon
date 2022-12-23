package com.yotfr.sunmoon.presentation.notes.archive_note.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemArchiveNoteListFooterBinding
import com.yotfr.sunmoon.presentation.notes.archive_note.model.ArchiveNoteFooterModel

class ArchiveNoteListFooterAdapter :
    RecyclerView.Adapter<ArchiveNoteListFooterAdapter.ArchiveNoteListFooterViewHolder>() {

    var footerState: ArchiveNoteFooterModel = ArchiveNoteFooterModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_archive_note_list_footer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveNoteListFooterViewHolder {
        return ArchiveNoteListFooterViewHolder(
            ItemArchiveNoteListFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArchiveNoteListFooterViewHolder, position: Int) {
        holder.bind(footerState)
    }

    class ArchiveNoteListFooterViewHolder(
        private val binding: ItemArchiveNoteListFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: ArchiveNoteFooterModel) {
            binding.scheduledFooter.visibility = if (
                headerState.isVisible
            ) View.VISIBLE else View.GONE
        }
    }
}
