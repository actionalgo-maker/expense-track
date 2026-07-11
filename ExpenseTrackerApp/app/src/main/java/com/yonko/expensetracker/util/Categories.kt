package com.yonko.expensetracker.util

import android.graphics.Color

data class Category(val id: String, val label: String, val emoji: String, val color: Int)

object Categories {
    val ALL = listOf(
        Category("food", "Food", "🍔", Color.parseColor("#FF8A5C")),
        Category("transport", "Transport", "🚗", Color.parseColor("#5AC8FA")),
        Category("shopping", "Shopping", "🛍️", Color.parseColor("#C77DFF")),
        Category("bills", "Bills", "🧾", Color.parseColor("#FFD166")),
        Category("entertainment", "Fun", "🎬", Color.parseColor("#FF5C8A")),
        Category("other", "Other", "📦", Color.parseColor("#8A8FA3"))
    )

    fun byId(id: String): Category = ALL.firstOrNull { it.id == id } ?: ALL.last()
}
