package com.metapointer.expensetrackerapp.domain.repository

import com.metapointer.expensetrackerapp.data.model.DailyExpenseSummary
import com.metapointer.expensetrackerapp.data.model.Expense
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    fun getTodayExpenses(): Flow<List<Expense>>
    suspend fun getTodayTotal(): Double
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>
    suspend fun insertExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun getDailyExpenseSummary(startDate: Long, endDate: Long): List<DailyExpenseSummary>
}