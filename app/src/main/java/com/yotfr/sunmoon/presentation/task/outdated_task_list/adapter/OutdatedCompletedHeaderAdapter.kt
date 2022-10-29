package com.yotfr.sunmoon.presentation.task.outdated_task_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.databinding.ItemOutdatedCompletedTaskHeaderBinding
import com.yotfr.sunmoon.presentation.task.outdated_task_list.model.OutdatedCompletedHeaderStateModel

interface OutdatedCompletedHeaderDelegate {
    fun hideCompleted()
}

class OutdatedCompletedHeaderAdapter : RecyclerView.Adapter<OutdatedCompletedHeaderAdapter.OutdatedTaskHeaderViewHolder>() {

    private var delegate: OutdatedCompletedHeaderDelegate? = null

    fun attachDelegate(delegate: OutdatedCompletedHeaderDelegate) {
        this.delegate = delegate
    }

    var headerState: OutdatedCompletedHeaderStateModel = OutdatedCompletedHeaderStateModel()
        set(newValue) {
            field = newValue
            notifyItemChanged(0)
        }

    private fun notifyChanges() = View.OnClickListener { notifyDataSetChanged() }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_outdated_completed_task_header
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutdatedTaskHeaderViewHolder {
        return OutdatedTaskHeaderViewHolder(
            ItemOutdatedCompletedTaskHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate, notifyChanges()
        )
    }

    override fun onBindViewHolder(holder: OutdatedTaskHeaderViewHolder, position: Int) {
        holder.bind(headerState)
    }

    class OutdatedTaskHeaderViewHolder(
        private val binding: ItemOutdatedCompletedTaskHeaderBinding,
        private val delegate:OutdatedCompletedHeaderDelegate?,
        private val onClickListener: View.OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headerState:OutdatedCompletedHeaderStateModel) {
            binding.llOutdatedCompletedHeader.visibility = if (headerState.isVisible) View.VISIBLE
            else View.GONE
            binding.btnOutdatedCompletedHeaderExpand.setOnClickListener {
                delegate?.hideCompleted()
                onClickListener.onClick(it)
            }
        }
    }
}