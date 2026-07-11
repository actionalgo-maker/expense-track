package com.yonko.expensetracker.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yonko.expensetracker.databinding.ItemDateHeaderBinding
import com.yonko.expensetracker.databinding.ItemExpenseBinding
import com.yonko.expensetracker.util.Categories
import com.yonko.expensetracker.util.Formatting

class ExpenseAdapter(
    private val onDelete: (com.yonko.expensetracker.data.Expense) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ExpenseListItem>()

    fun submit(newItems: List<ExpenseListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (items[position] is ExpenseListItem.Header) TYPE_HEADER else TYPE_ROW

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderVH(ItemDateHeaderBinding.inflate(inflater, parent, false))
        } else {
            RowVH(ItemExpenseBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ExpenseListItem.Header -> (holder as HeaderVH).bind(item)
            is ExpenseListItem.Row -> (holder as RowVH).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderVH(private val b: ItemDateHeaderBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: ExpenseListItem.Header) {
            b.dateLabel.text = item.label
        }
    }

    inner class RowVH(private val b: ItemExpenseBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: ExpenseListItem.Row) {
            val e = item.expense
            val cat = Categories.byId(e.category)
            b.expenseIcon.text = cat.emoji
            b.expenseIcon.setBackgroundColor(Color.argb(38, Color.red(cat.color), Color.green(cat.color), Color.blue(cat.color)))
            b.expenseTitle.text = e.note.ifBlank { cat.label }
            b.expenseSubtitle.text = "${cat.label} · ${e.method}"
            b.expenseAmount.text = Formatting.money(e.amount)
            b.root.setOnClickListener { onDelete(e) }
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ROW = 1
    }
}
