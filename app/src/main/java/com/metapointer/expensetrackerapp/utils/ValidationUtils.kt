package com.metapointer.expensetrackerapp.utils


// Additional validation utilities
object ValidationUtils {

    fun sanitizeInput(input: String): String {
        return input.trim()
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .take(1000) // Limit input length for security
    }

    fun isValidExpenseTitle(title: String): Boolean {
        return title.isNotBlank() &&
                title.length >= 3 &&
                title.length <= 50 &&
                !title.matches(Regex(".*[<>\"'&].*"))
    }

    fun formatAmountForDisplay(amount: Double): String {
        return String.format("%.2f", amount)
    }

    fun parseAmount(amountString: String): Double? {
        return try {
            amountString.trim().toDoubleOrNull()?.let { amount ->
                if (amount >= 0.01 && amount <= 999999.99) amount else null
            }
        } catch (e: Exception) {
            null
        }
    }
}