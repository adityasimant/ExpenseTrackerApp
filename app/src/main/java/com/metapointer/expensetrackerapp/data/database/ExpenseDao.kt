package com.metapointer.expensetrackerapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.metapointer.expensetrackerapp.data.model.DailyExpenseSummary
import com.metapointer.expensetrackerapp.data.model.Expense
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :startOfDay AND date < :endOfDay ORDER BY date DESC")
    fun getTodayExpenses(startOfDay: Long, endOfDay: Long): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startOfDay AND date < :endOfDay")
    suspend fun getTodayTotal(startOfDay: Long, endOfDay: Long): Double?

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    // For reporting
    @Query("""
        SELECT 
            DATE(date/1000, 'unixepoch') as date,
            SUM(amount) as totalAmount,
            COUNT(*) as expenseCount
        FROM expenses 
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY DATE(date/1000, 'unixepoch')
        ORDER BY date DESC
    """)
    suspend fun getDailyExpenseSummary(startDate: Long, endDate: Long): List<DailyExpenseSummary>
}