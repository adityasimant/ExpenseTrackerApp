package com.metapointer.expensetrackerapp.ui.screens.entry

import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import com.metapointer.expensetrackerapp.ui.screens.entry.model.FormValidationState

data class ExpenseEntryUiState(
    val title: String = "",
    val amount: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.FOOD,
    val notes: String = "",
    val receiptImagePath: String? = null,
    val isLoading: Boolean = false,
    val isExpenseAdded: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val validationState: FormValidationState = FormValidationState()
)