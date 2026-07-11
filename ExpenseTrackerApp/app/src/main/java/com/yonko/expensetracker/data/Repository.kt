package com.yonko.expensetracker.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class Repository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val expenseDao = db.expenseDao()
    private val budgetDao = db.budgetDao()

    fun observeExpenses(): Flow<List<Expense>> = expenseDao.observeAll()
    fun observeBudgets(): Flow<List<Budget>> = budgetDao.observeAll()

    suspend fun addExpense(expense: Expense) = expenseDao.insert(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)
    suspend fun setBudget(category: String, amount: Double) =
        budgetDao.upsert(Budget(category, amount))

    suspend fun getAllExpensesOnce(): List<Expense> = expenseDao.getAll()

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(context: Context): Repository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(context).also { INSTANCE = it }
            }
        }
    }
}
