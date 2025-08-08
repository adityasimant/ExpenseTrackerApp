package com.metapointer.expensetrackerapp.ui.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metapointer.expensetrackerapp.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExpenseReportViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseReportUiState())
    val uiState: StateFlow<ExpenseReportUiState> = _uiState.asStateFlow()

    init {
        loadLastWeekReport()
    }

    fun loadLastWeekReport() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val calendar = Calendar.getInstance()
                val endDate = calendar.timeInMillis

                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val startDate = calendar.timeInMillis

                val summary = repository.getDailyExpenseSummary(startDate, endDate)
                val totalWeekExpense = summary.sumOf { it.totalAmount }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dailySummaries = summary,
                        totalWeekExpense = totalWeekExpense
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load report: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadCustomDateReport(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val summary = repository.getDailyExpenseSummary(startDate, endDate)
                val totalExpense = summary.sumOf { it.totalAmount }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dailySummaries = summary,
                        totalWeekExpense = totalExpense
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load report: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}