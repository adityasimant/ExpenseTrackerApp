package com.metapointer.expensetrackerapp.data.repository

import com.metapointer.expensetrackerapp.data.database.ExpenseDao
import com.metapointer.expensetrackerapp.data.model.DailyExpenseSummary
import com.metapointer.expensetrackerapp.data.model.Expense
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import com.metapointer.expensetrackerapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses()
    }

    override fun getTodayExpenses(): Flow<List<Expense>> {
        val startOfDay = getTodayStartTime()
        val endOfDay = getTodayEndTime()
        return expenseDao.getTodayExpenses(startOfDay, endOfDay)
    }

    override suspend fun getTodayTotal(): Double {
        val startOfDay = getTodayStartTime()
        val endOfDay = getTodayEndTime()
        return expenseDao.getTodayTotal(startOfDay, endOfDay) ?: 0.0
    }

    override fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }

    override fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }

    override suspend fun insertExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    override suspend fun getDailyExpenseSummary(startDate: Long, endDate: Long): List<DailyExpenseSummary> {
        return expenseDao.getDailyExpenseSummary(startDate, endDate)
    }

    private fun getTodayStartTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getTodayEndTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}