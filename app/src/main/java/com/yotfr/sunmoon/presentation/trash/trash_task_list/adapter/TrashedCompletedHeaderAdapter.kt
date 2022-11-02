package com.yotfr.sunmoon.presentation.trash.trash_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemTrashedCompletedTaskHeaderBinding
import com.yotfr.sunmoon.presentation.trash.trash_task_list.model.TrashedCompletedHeaderStateModel

interface TrashedCompletedHeaderDelegate {
    fun hideCompleted()
}

class TrashedCompletedHeaderAdapter : RecyclerView.Adapter<TrashedCompletedHeaderAdapter.CompletedTaskHeaderViewHolder>() {

    private var delegate: TrashedCompletedHeaderDelegate? = null

    fun attachDelegate(delegate: TrashedCompletedHeaderDelegate) {
        this.delegate = delegate
    }

    var headerState: TrashedCompletedHeaderStateModel = TrashedCompletedHeaderStateModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_trashed_completed_task_header
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskHeaderViewHolder {
        return CompletedTaskHeaderViewHolder(
            ItemTrashedCompletedTaskHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: CompletedTaskHeaderViewHolder, position: Int) {
        holder.bind(headerState)
    }

    class CompletedTaskHeaderViewHolder(
        private val binding: ItemTrashedCompletedTaskHeaderBinding,
        private val delegate:TrashedCompletedHeaderDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: TrashedCompletedHeaderStateModel) {
            binding.llTrashedCompletedHeader.visibility = if (headerState.isVisible) View.VISIBLE
            else View.GONE
            binding.btnTrashedCompletedHeaderExpand.isChecked = headerState.isExpanded
            binding.btnTrashedCompletedHeaderExpand.setOnClickListener {
                delegate?.hideCompleted()
            }
        }
    }
}