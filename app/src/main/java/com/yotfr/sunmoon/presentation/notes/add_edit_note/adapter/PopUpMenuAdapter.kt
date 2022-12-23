package com.yotfr.sunmoon.presentation.notes.add_edit_note.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.yotfr.sunmoon.databinding.ItemPopUpMenuBinding
import com.yotfr.sunmoon.databinding.ItemPopUpMenuFooterBinding
import com.yotfr.sunmoon.presentation.notes.add_edit_note.model.AddEditNoteCategoryModel

interface PopUpDelegate {
    fun addCategoryPressed()
}

class PopUpMenuAdapter : BaseAdapter() {

    private var delegate: PopUpDelegate? = null

    fun attachDelegate(delegate: PopUpDelegate) {
        this.delegate = delegate
    }

    private val footer = 0
    private val mainList = 1

    var categories: List<AddEditNoteCategoryModel> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun getCount(): Int = categories.size + 1

    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            categories.size -> footer
            else -> mainList
        }
    }

    override fun getItem(position: Int): Any = categories[position]

    override fun getItemId(position: Int): Long {
        return when (position) {
            categories.size -> FOOTER_ID
            else -> categories[position].id!!
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return when (getItemViewType(position)) {
            mainList -> {
                val binding = convertView?.tag as ItemPopUpMenuBinding?
                    ?: createMainListBinding(parent.context)
                val category = getItem(position) as AddEditNoteCategoryModel
                binding.tvPopUpMenuItem.text = category.categoryDescription
                binding.tvPopUpMenuNoteCount.text = if (category.notesCount != null) {
                    category.notesCount.toString()
                } else ""
                binding.tvPopUpMenuItem.tag = category
                binding.root
            }
            footer -> {
                val binding = convertView?.tag as ItemPopUpMenuFooterBinding?
                    ?: createFooterBinding(parent.context)
                binding.btnAddCategoryPopUp.setOnClickListener {
                    delegate?.addCategoryPressed()
                }
                binding.root
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    private fun createMainListBinding(context: Context): ItemPopUpMenuBinding {
        val binding = ItemPopUpMenuBinding.inflate(LayoutInflater.from(context))
        binding.root.tag = binding
        return binding
    }

    private fun createFooterBinding(context: Context): ItemPopUpMenuFooterBinding {
        val binding = ItemPopUpMenuFooterBinding.inflate(LayoutInflater.from(context))
        binding.root.tag = binding
        return binding
    }

    companion object {
        const val FOOTER_ID = -1L
    }
}
