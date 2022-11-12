package com.yotfr.sunmoon.presentation.trash.trash_note_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.*
import com.yotfr.sunmoon.presentation.trash.trash_note_list.model.TrashNoteModel

class TrashNoteListDiffCallBack(
    private val oldList: List<TrashNoteModel>,
    private val newList: List<TrashNoteModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}


class TrashNotesAdapter : RecyclerView.Adapter<TrashNotesAdapter.DeletedNoteViewHolder>() {

    var deletedNotes: List<TrashNoteModel> = emptyList()
        set(newValue) {
            val diffCallback = TrashNoteListDiffCallBack(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

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
        holder.bind(deletedNotes[position])
    }

    override fun getItemCount(): Int = deletedNotes.size


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











