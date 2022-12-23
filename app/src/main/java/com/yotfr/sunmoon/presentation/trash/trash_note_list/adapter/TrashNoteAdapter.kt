package com.yotfr.sunmoon.presentation.trash.trash_note_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.*
import com.yotfr.sunmoon.presentation.trash.trash_note_list.model.TrashNoteModel

class TrashNoteListDiffCallBack : DiffUtil.ItemCallback<TrashNoteModel>() {

    override fun areItemsTheSame(oldItem: TrashNoteModel, newItem: TrashNoteModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TrashNoteModel, newItem: TrashNoteModel): Boolean {
        return oldItem == newItem
    }
}

class TrashNotesAdapter : ListAdapter<TrashNoteModel, TrashNotesAdapter.DeletedNoteViewHolder>(
    TrashNoteListDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletedNoteViewHolder {
        return DeletedNoteViewHolder(
            ItemTrashedNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DeletedNoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_trashed_note
    }

    class DeletedNoteViewHolder(
        private val binding: ItemTrashedNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: TrashNoteModel) {
            binding.apply {
                tvDeletedNoteDescription.text = note.text
                tvDeletedNoteTitle.text = note.title
                tvDeletedNoteDate.text = note.createdAt
            }
        }
    }
}
