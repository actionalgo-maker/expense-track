package com.yonko.expensetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(budget: Budget)

    @Query("SELECT * FROM budgets")
    fun observeAll(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets")
    suspend fun getAll(): List<Budget>
}
