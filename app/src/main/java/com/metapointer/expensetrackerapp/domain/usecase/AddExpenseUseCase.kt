package com.metapointer.expensetrackerapp.domain.usecase

import com.metapointer.expensetrackerapp.data.model.Expense
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import com.metapointer.expensetrackerapp.domain.repository.ExpenseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        title: String,
        amount: Double,
        category: ExpenseCategory,
        notes: String = "",
        receiptImagePath: String? = null
    ): Result<Long> {
        return try {
            if (title.isBlank()) {
                Result.failure(IllegalArgumentException("Title cannot be empty"))
            } else if (amount <= 0) {
                Result.failure(IllegalArgumentException("Amount must be greater than 0"))
            } else {
                val expense = Expense(
                    title = title.trim(),
                    amount = amount,
                    category = category,
                    notes = notes.trim(),
                    receiptImagePath = receiptImagePath
                )
                val id = repository.insertExpense(expense)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}