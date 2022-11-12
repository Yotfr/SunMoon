package com.yotfr.sunmoon.presentation.notes.archive_note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemArchiveNoteBinding
import com.yotfr.sunmoon.presentation.notes.archive_note.model.ArchiveNoteModel

interface ArchiveNoteListDelegate {
    fun noteDetailsClicked(id: Long)
}

class ArchiveNoteListDiffCallBack: DiffUtil.ItemCallback<ArchiveNoteModel>() {

    override fun areItemsTheSame(oldItem: ArchiveNoteModel, newItem: ArchiveNoteModel): Boolean {
        return oldItem.id == newItem.id

    }

    override fun areContentsTheSame(oldItem: ArchiveNoteModel, newItem: ArchiveNoteModel): Boolean {
        return oldItem == newItem

    }
}

class ArchiveNoteAdapter : ListAdapter<ArchiveNoteModel,ArchiveNoteAdapter.ArchiveNoteViewHolder>(
    ArchiveNoteListDiffCallBack()
) {

    private var delegate: ArchiveNoteListDelegate? = null

    fun attachDelegate(delegate: ArchiveNoteListDelegate) {
        this.delegate = delegate
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
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_note
    }

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