package com.nagz.money_manager.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nagz.money_manager.R
import com.google.android.material.card.MaterialCardView



class ColorAdapter(
    private val colors: List<String>,
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorHex = colors[position]
        holder.bind(colorHex, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onColorSelected(colorHex)
        }
    }

    override fun getItemCount(): Int = colors.size

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorView: View = itemView.findViewById(R.id.colorView)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.colorCard)

        fun bind(colorHex: String, isSelected: Boolean) {
            colorView.setBackgroundColor(Color.parseColor(colorHex))

            if (isSelected) {
                cardView.strokeColor = Color.WHITE
                cardView.strokeWidth = 6
            } else {
                cardView.strokeWidth = 0
            }
        }
    }
}