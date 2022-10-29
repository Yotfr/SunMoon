package com.yotfr.sunmoon.presentation.notes.note_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemNoteBinding
import com.yotfr.sunmoon.presentation.notes.note_list.model.NoteListModel


interface NoteListDelegate {
    fun noteDetailsClicked(noteId: Long)
    fun pinPressed(note:NoteListModel)
}

class NoteListDiffCallBack(
    private val oldList: List<NoteListModel>,
    private val newList: List<NoteListModel>
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


class NoteListAdapter : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    private var delegate: NoteListDelegate? = null

    fun attachDelegate(delegate: NoteListDelegate) {
        this.delegate = delegate
    }

    var notes: List<NoteListModel> = emptyList()
        set(newValue) {
            val diffCallback = NoteListDiffCallBack(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemViewType(position: Int): Int {
        return  R.layout.item_note
    }

    override fun getItemCount() = notes.size

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
                       noteId =  note.id
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

