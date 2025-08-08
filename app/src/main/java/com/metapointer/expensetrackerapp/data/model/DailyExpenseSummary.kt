package com.metapointer.expensetrackerapp.data.model

data class DailyExpenseSummary(
    val date: String,
    val totalAmount: Double,
    val expenseCount: Int
)