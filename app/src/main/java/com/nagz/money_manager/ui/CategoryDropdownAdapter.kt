package com.nagz.money_manager.ui
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nagz.money_manager.R
import com.nagz.money_manager.data.local.entity.CategoryEntity
import java.util.*
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.ImageView

class CategoryDropdownAdapter(
    context: Context,
    private var allCategories: List<CategoryEntity>,
    private var iconMap: Map<String, Int> = emptyMap() // new
) : ArrayAdapter<CategoryEntity>(context, R.layout.item_category_dropdown, allCategories) {

    // Internal list for filtering
    private var filteredList: List<CategoryEntity> = allCategories

    override fun getCount(): Int = filteredList.size
    override fun getItem(position: Int): CategoryEntity? = filteredList[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_category_dropdown, parent, false)

        val icon = view.findViewById<ImageView>(R.id.imgIcon)
        val name = view.findViewById<TextView>(R.id.tvName)

        val category = getItem(position)

        name.text = category?.name ?: ""
        val iconRes = iconMap[category?.name] ?: R.drawable.ic_others
        icon.setImageResource(iconRes)


        return view
    }

    fun update(newCategories: List<CategoryEntity>, newIconMap: Map<String, Int>) {
        allCategories = newCategories
        iconMap = newIconMap
        notifyDataSetChanged()
    }



    // MANDATORY: Fixes the 'empty list on typing' bug
    override fun getFilter(): android.widget.Filter {
        return object : android.widget.Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val query = constraint?.toString()?.lowercase()

                val filtered = if (query.isNullOrEmpty()) {
                    allCategories
                } else {
                    allCategories.filter { it.name.lowercase().contains(query) }
                }

                results.values = filtered
                results.count = filtered.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<CategoryEntity> ?: allCategories
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as CategoryEntity).name
            }

        }


    }





}

