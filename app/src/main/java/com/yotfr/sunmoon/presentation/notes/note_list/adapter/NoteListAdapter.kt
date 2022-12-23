package com.yotfr.sunmoon.presentation.notes.note_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemNoteBinding
import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListModel

interface NoteListDelegate {
    fun noteDetailsClicked(noteId: Long)
    fun pinPressed(note: NoteListModel)
}

class NoteListDiffCallBack : DiffUtil.ItemCallback<NoteListModel>() {

    override fun areItemsTheSame(oldItem: NoteListModel, newItem: NoteListModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteListModel, newItem: NoteListModel): Boolean {
        return oldItem == newItem
    }
}

class NoteListAdapter : ListAdapter<NoteListModel, NoteListAdapter.NoteViewHolder>(
    NoteListDiffCallBack()
) {

    private var delegate: NoteListDelegate? = null

    fun attachDelegate(delegate: NoteListDelegate) {
        this.delegate = delegate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            delegate
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_note
    }

    class NoteViewHolder(
        private val binding: ItemNoteBinding,
        private val delegate: NoteListDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteListModel) {
            binding.apply {
                tvNoteDescription.text = note.text
                tvNoteTitle.text = note.title
                tvNoteDate.text = note.createdAt
                itemPinNote.isChecked = note.isPinned
                linearLayoutItemNote.setOnClickListener {
                    delegate?.noteDetailsClicked(
                        noteId = note.id
                    )
                }
                itemPinNote.setOnClickListener {
                    delegate?.pinPressed(
                        note = note
                    )
                }
            }
        }
    }
}
