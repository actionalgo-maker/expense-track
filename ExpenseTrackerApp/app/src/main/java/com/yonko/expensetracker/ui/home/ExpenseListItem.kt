package com.yonko.expensetracker.ui.home

import com.yonko.expensetracker.data.Expense

sealed class ExpenseListItem {
    data class Header(val label: String) : ExpenseListItem()
    data class Row(val expense: Expense) : ExpenseListItem()
}
