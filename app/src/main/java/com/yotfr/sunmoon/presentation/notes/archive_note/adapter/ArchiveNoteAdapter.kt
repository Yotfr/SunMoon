package com.yotfr.sunmoon.presentation.notes.archive_note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemArchiveNoteBinding
import com.yotfr.sunmoon.presentation.notes.archive_note.model.ArchiveNoteModel

interface ArchiveNoteListDelegate {
    fun noteDetailsClicked(id: Long?)
}

class ArchiveNoteListDiffCallBack(
    private val oldList: List<ArchiveNoteModel>,
    private val newList: List<ArchiveNoteModel>
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

class ArchiveNoteAdapter : RecyclerView.Adapter<ArchiveNoteAdapter.ArchiveNoteViewHolder>() {

    private var delegate: ArchiveNoteListDelegate? = null

    fun attachDelegate(delegate: ArchiveNoteListDelegate) {
        this.delegate = delegate
    }

    var notes: List<ArchiveNoteModel> = emptyList()
        set(newValue) {
            val diffCallback = ArchiveNoteListDiffCallBack(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveNoteViewHolder {
        return ArchiveNoteViewHolder(
            ItemArchiveNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: ArchiveNoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_note
    }

    override fun getItemCount() = notes.size

    class ArchiveNoteViewHolder(
        private val binding: ItemArchiveNoteBinding,
        private val delegate: ArchiveNoteListDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: ArchiveNoteModel) {
            binding.apply {
                tvArchiveNoteDescription.text = note.text
                tvArchiveNoteTitle.text = note.title
                tvArchiveNoteDate.text = note.createdAt
                itemArchiveNoteCard.setOnClickListener {
                    delegate?.noteDetailsClicked(note.id)
                }
            }
        }
    }
}