package com.metapointer.expensetrackerapp.ui.screens.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import com.metapointer.expensetrackerapp.domain.usecase.AddExpenseUseCase
import com.metapointer.expensetrackerapp.domain.usecase.GetTodayTotalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getTodayTotalUseCase: GetTodayTotalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseEntryUiState())
    val uiState: StateFlow<ExpenseEntryUiState> = _uiState.asStateFlow()

    private val _todayTotal = MutableStateFlow(0.0)
    val todayTotal: StateFlow<Double> = _todayTotal.asStateFlow()

    init {
        loadTodayTotal()
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onAmountChanged(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onCategorySelected(category: ExpenseCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onNotesChanged(notes: String) {
        if (notes.length <= 100) {
            _uiState.update { it.copy(notes = notes) }
        }
    }

    fun onReceiptImageSelected(imagePath: String?) {
        _uiState.update { it.copy(receiptImagePath = imagePath) }
    }

    fun addExpense() {
        val currentState = _uiState.value

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val amount = currentState.amount.toDoubleOrNull()
                if (amount == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Please enter a valid amount")
                    }
                    return@launch
                }

                addExpenseUseCase(
                    title = currentState.title,
                    amount = amount,
                    category = currentState.selectedCategory,
                    notes = currentState.notes,
                    receiptImagePath = currentState.receiptImagePath
                ).fold(
                    onSuccess = { id ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isExpenseAdded = true,
                                successMessage = "Expense added successfully!"
                            )
                        }
                        loadTodayTotal() // Refresh today's total
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Failed to add expense"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun clearSuccessState() {
        _uiState.update {
            ExpenseEntryUiState(selectedCategory = _uiState.value.selectedCategory)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadTodayTotal() {
        viewModelScope.launch {
            try {
                val total = getTodayTotalUseCase()
                _todayTotal.value = total
            } catch (e: Exception) {
                // Handle error silently for today's total
            }
        }
    }
}