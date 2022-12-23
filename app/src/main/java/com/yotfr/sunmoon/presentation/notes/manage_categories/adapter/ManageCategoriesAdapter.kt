package com.yotfr.sunmoon.presentation.notes.manage_categories.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.databinding.ItemCategoryManageBinding
import com.yotfr.sunmoon.presentation.notes.manage_categories.model.ManageCategoriesModel

interface CategoriesDelegate {
    fun editCategoryPressed(category: ManageCategoriesModel)
    fun deleteCategoryPressed(category: ManageCategoriesModel)
    fun changeCategoryVisibility(category: ManageCategoriesModel)
}

class CategoryListDiffCallBack : DiffUtil.ItemCallback<ManageCategoriesModel>() {

    override fun areItemsTheSame(
        oldItem: ManageCategoriesModel,
        newItem: ManageCategoriesModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ManageCategoriesModel,
        newItem: ManageCategoriesModel
    ): Boolean {
        return oldItem == newItem
    }
}

class ManageCategoriesAdapter :
    ListAdapter<ManageCategoriesModel, ManageCategoriesAdapter.CategoriesViewHolder>(
        CategoryListDiffCallBack()
    ) {

    private var delegate: CategoriesDelegate? = null

    fun attachDelegate(delegate: CategoriesDelegate) {
        this.delegate = delegate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            ItemCategoryManageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            delegate
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoriesViewHolder(
        private val binding: ItemCategoryManageBinding,
        private val delegate: CategoriesDelegate?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: ManageCategoriesModel) {
            binding.apply {
                tvCategoryDescription.text = category.categoryDescription
                cbVisibility.isChecked = category.isVisible
                noteCount.text = category.notesCount.toString()
                btnEditCategory.setOnClickListener {
                    delegate?.editCategoryPressed(
                        category = category
                    )
                }
                btnDeleteCategory.setOnClickListener {
                    delegate?.deleteCategoryPressed(
                        category = category
                    )
                }
                cbVisibility.setOnClickListener {
                    delegate?.changeCategoryVisibility(
                        category = category
                    )
                }
            }
        }
    }
}
