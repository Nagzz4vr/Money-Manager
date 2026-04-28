package com.nagz.money_manager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nagz.money_manager.R
import com.nagz.money_manager.data.local.entity.CategoryEntity

class ManageCategoryAdapter(private val onDeleteClick: (CategoryEntity) -> Unit) :
    ListAdapter<CategoryEntity, ManageCategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, onDeleteClick)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivIcon = view.findViewById<ImageView>(R.id.ivCategoryIcon)
        private val tvName = view.findViewById<TextView>(R.id.tvCategoryName)
        private val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        fun bind(category: CategoryEntity, onDeleteClick: (CategoryEntity) -> Unit) {
            tvName.text = category.name
            ivIcon.setImageResource(category.iconRes)
            ivIcon.setColorFilter(category.color)

            btnDelete.setOnClickListener { onDeleteClick(category) }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryEntity>() {
        override fun areItemsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity) = oldItem.categoryId == newItem.categoryId
        override fun areContentsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity) = oldItem == newItem
    }
}