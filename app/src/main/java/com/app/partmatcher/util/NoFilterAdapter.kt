package com.app.partmatcher.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class NoFilterAdapter(context: Context, layout: Int, items: List<String>) :
    ArrayAdapter<String>(context, layout, items) {

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = items
            results.count = items.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = filter
    
    fun updateItems(newItems: List<String>) {
        clear()
        addAll(newItems)
        notifyDataSetChanged()
    }
}
