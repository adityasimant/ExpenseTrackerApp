package com.metapointer.expensetrackerapp.ui.screens.entry

import com.metapointer.expensetrackerapp.data.model.ExpenseCategory
import com.metapointer.expensetrackerapp.ui.screens.entry.model.FormValidationState
import com.metapointer.expensetrackerapp.ui.screens.entry.model.ValidationResult

object ExpenseFormValidator {

    private const val MAX_TITLE_LENGTH = 50
    private const val MAX_NOTES_LENGTH = 200
    private const val MIN_AMOUNT = 0.01
    private const val MAX_AMOUNT = 999999.99

    fun validateTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult(false, "Title is required")
            title.length < 3 -> ValidationResult(false, "Title must be at least 3 characters")
            title.length > MAX_TITLE_LENGTH -> ValidationResult(false, "Title must not exceed $MAX_TITLE_LENGTH characters")
            !title.matches(Regex("^[a-zA-Z0-9\\s\\-_.,()]+$")) ->
                ValidationResult(false, "Title contains invalid characters")
            else -> ValidationResult(true)
        }
    }

    fun validateAmount(amountString: String): ValidationResult {
        return when {
            amountString.isBlank() -> ValidationResult(false, "Amount is required")
            !isValidAmountFormat(amountString) -> ValidationResult(false, "Please enter a valid amount")
            else -> {
                val amount = amountString.toDoubleOrNull()
                when {
                    amount == null -> ValidationResult(false, "Invalid amount format")
                    amount < MIN_AMOUNT -> ValidationResult(false, "Amount must be at least ₹$MIN_AMOUNT")
                    amount > MAX_AMOUNT -> ValidationResult(false, "Amount cannot exceed ₹$MAX_AMOUNT")
                    !isValidDecimalPlaces(amountString) ->
                        ValidationResult(false, "Amount can have at most 2 decimal places")
                    else -> ValidationResult(true)
                }
            }
        }
    }

    fun validateCategory(category: ExpenseCategory?): ValidationResult {
        return if (category == null) {
            ValidationResult(false, "Please select a category")
        } else {
            ValidationResult(true)
        }
    }

    fun validateNotes(notes: String): ValidationResult {
        return when {
            notes.length > MAX_NOTES_LENGTH ->
                ValidationResult(false, "Notes must not exceed $MAX_NOTES_LENGTH characters")
            notes.contains(Regex("[<>\"'&]")) ->
                ValidationResult(false, "Notes contain invalid characters")
            else -> ValidationResult(true)
        }
    }

    private fun isValidAmountFormat(amount: String): Boolean {
        // Allow numbers with optional decimal point and up to 2 decimal places
        return amount.matches(Regex("^\\d+(\\.\\d{1,2})?$"))
    }

    private fun isValidDecimalPlaces(amount: String): Boolean {
        val decimalIndex = amount.indexOf('.')
        return if (decimalIndex == -1) {
            true // No decimal point
        } else {
            amount.length - decimalIndex - 1 <= 2 // At most 2 decimal places
        }
    }

    fun validateForm(
        title: String,
        amount: String,
        category: ExpenseCategory?,
        notes: String
    ): FormValidationState {
        val titleValidation = validateTitle(title)
        val amountValidation = validateAmount(amount)
        val categoryValidation = validateCategory(category)
        val notesValidation = validateNotes(notes)

        val isFormValid = titleValidation.isValid &&
                amountValidation.isValid &&
                categoryValidation.isValid &&
                notesValidation.isValid

        return FormValidationState(
            titleValidation = titleValidation,
            amountValidation = amountValidation,
            categoryValidation = categoryValidation,
            notesValidation = notesValidation,
            isFormValid = isFormValid
        )
    }
}