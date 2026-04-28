package com.nagz.money_manager.ui

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.nagz.money_manager.R
import com.nagz.money_manager.data.local.entity.TransactionEntity
import com.nagz.money_manager.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*
import com.nagz.money_manager.data.local.dao.TransactionWithIcon
import com.nagz.money_manager.data.local.dao.CategoryDao
import com.nagz.money_manager.data.local.database.CategoryDatabase
import com.nagz.money_manager.data.repository.CategoryRepository
import kotlin.getValue
import  androidx.fragment.app.viewModels
import  androidx.core.content.ContentProviderCompat.requireContext

class TransactionAdapter(
    private var items: List<TransactionEntity>,
    private var iconMap: Map<String, Int>,
    private val onItemClick: (TransactionEntity) -> Unit,
    private val onItemLongClick: (TransactionEntity) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TxViewHolder>(){

    private val iconCache = mutableMapOf<String, Int>()


    inner class TxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)

        val icon = view.findViewById<ImageView>(R.id.imgIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TxViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_details, parent, false)
        return TxViewHolder(view)
    }

    override fun onBindViewHolder(holder: TxViewHolder, position: Int) {
        val tx = items[position]

        holder.tvTitle.text = tx.category
        holder.tvDate.text = formatDate(tx.date)
        holder.tvAmount.text = "₹ ${tx.amount}"

        val iconRes = iconMap[tx.category] ?: R.drawable.ic_others
        holder.icon.setImageResource(iconRes)


        holder.tvAmount.setTextColor(
            if (tx.type == TransactionType.RECEIVED)
                ContextCompat.getColor(holder.itemView.context, R.color.positive)
            else
                ContextCompat.getColor(holder.itemView.context, R.color.accent_primary)
        )

        holder.itemView.setOnClickListener { onItemClick(tx) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(tx)
            true
        }
    }



    override fun getItemCount() = items.size

    fun update(newItems: List<TransactionEntity>, newIconMap: Map<String, Int>) {
        items = newItems
        iconMap = newIconMap
        notifyDataSetChanged()
    }



    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}