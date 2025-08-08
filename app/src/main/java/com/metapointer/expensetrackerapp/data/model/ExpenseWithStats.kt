package com.metapointer.expensetrackerapp.data.model

data class ExpenseWithStats(
    val expense: Expense,
    val isToday: Boolean = false
)