package com.yonko.expensetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: Expense): Long

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC, createdAt DESC")
    fun observeAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC, createdAt DESC")
    suspend fun getAll(): List<Expense>

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)
}
