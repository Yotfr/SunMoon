package com.yotfr.sunmoon.presentation.notes.manage_categories.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yotfr.sunmoon.databinding.ItemCategoryManageBinding
import com.yotfr.sunmoon.presentation.notes.manage_categories.model.ManageCategoriesModel

interface CategoriesDelegate {
    fun editCategoryPressed(category: ManageCategoriesModel)
    fun deleteCategoryPressed(category: ManageCategoriesModel)
    fun changeCategoryVisibility(category: ManageCategoriesModel)
}

class CategoryListDiffCallBack(
    private val oldList: List<ManageCategoriesModel>,
    private val newList: List<ManageCategoriesModel>
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

class ManageCategoriesAdapter :
    RecyclerView.Adapter<ManageCategoriesAdapter.CategoriesViewHolder>() {

    private var delegate: CategoriesDelegate? = null

    fun attachDelegate(delegate: CategoriesDelegate) {
        this.delegate = delegate
    }

    var categories: List<ManageCategoriesModel> = emptyList()
        set(newValue) {
            val diffCallback = CategoryListDiffCallBack(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            ItemCategoryManageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), delegate
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

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