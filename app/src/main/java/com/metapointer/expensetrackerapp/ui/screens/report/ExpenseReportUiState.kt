package com.metapointer.expensetrackerapp.ui.screens.report

import com.metapointer.expensetrackerapp.data.model.DailyExpenseSummary

data class ExpenseReportUiState(
    val isLoading: Boolean = false,
    val dailySummaries: List<DailyExpenseSummary> = emptyList(),
    val totalWeekExpense: Double = 0.0,
    val errorMessage: String? = null,
    val isDemoMode: Boolean = false
)