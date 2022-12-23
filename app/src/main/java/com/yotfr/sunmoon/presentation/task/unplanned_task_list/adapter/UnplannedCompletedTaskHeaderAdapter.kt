package com.yotfr.sunmoon.presentation.task.unplanned_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemUnplannedCompletedTaskHeaderBinding
import com.yotfr.sunmoon.presentation.task.unplanned_task_list.model.UnplannedCompletedHeaderStateModel

interface UnplannedCompletedHeaderDelegate {
    fun hideCompleted()
}

class UnplannedCompletedTaskHeaderAdapter : RecyclerView.Adapter<UnplannedCompletedTaskHeaderAdapter.CompletedTaskHeaderViewHolder>() {

    private var delegate: UnplannedCompletedHeaderDelegate? = null

    fun attachDelegate(delegate: UnplannedCompletedHeaderDelegate) {
        this.delegate = delegate
    }

    var headerState: UnplannedCompletedHeaderStateModel = UnplannedCompletedHeaderStateModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_unplanned_completed_task_header
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskHeaderViewHolder {
        return CompletedTaskHeaderViewHolder(
            ItemUnplannedCompletedTaskHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            delegate
        )
    }

    override fun onBindViewHolder(holder: CompletedTaskHeaderViewHolder, position: Int) {
        holder.bind(headerState)
    }

    class CompletedTaskHeaderViewHolder(
        private val binding: ItemUnplannedCompletedTaskHeaderBinding,
        private val delegate: UnplannedCompletedHeaderDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState: UnplannedCompletedHeaderStateModel) {
            binding.llUnplannedCompletedHeader.visibility = if (headerState.isVisible) View.VISIBLE
            else View.GONE
            binding.btnUnplannedCompletedHeaderExpand.isChecked = headerState.isExpanded
            binding.btnUnplannedCompletedHeaderExpand.setOnClickListener {
                delegate?.hideCompleted()
            }
        }
    }
}
