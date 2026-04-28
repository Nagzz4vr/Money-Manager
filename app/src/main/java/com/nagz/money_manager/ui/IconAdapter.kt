package com.nagz.money_manager.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nagz.money_manager.R
import com.google.android.material.card.MaterialCardView

class IconAdapter(
    private val icons: List<Int>,
    private val onIconSelected: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    // Track which icon is currently selected (-1 means none)
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconRes = icons[position]
        holder.bind(iconRes, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition

            // Refresh the old and new items to update their borders
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            // Pass the selected resource ID back to the Fragment
            onIconSelected(iconRes)
        }
    }

    override fun getItemCount(): Int = icons.size

    class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)

        // FIX: Must be MaterialCardView to support strokeColor and strokeWidth
        private val cardView: MaterialCardView = itemView.findViewById(R.id.iconCard)

        fun bind(iconRes: Int, isSelected: Boolean) {
            ivIcon.setImageResource(iconRes)

            if (isSelected) {
                // Selected State: Purple border and white icon
                cardView.strokeColor = Color.parseColor("#7F35FF")
                cardView.strokeWidth = 4
                ivIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                // Default State: No border and greyish icon
                cardView.strokeWidth = 0
                ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#99FFFFFF"))
            }
        }
    }
}