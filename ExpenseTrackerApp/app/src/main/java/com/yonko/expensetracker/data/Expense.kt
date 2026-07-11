package com.yonko.expensetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String,
    val note: String,
    /** ISO date string, yyyy-MM-dd */
    val date: String,
    val method: String,
    val createdAt: Long = System.currentTimeMillis()
)
