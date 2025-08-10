package com.metapointer.expensetrackerapp.utils

import java.text.DecimalFormat
import java.text.NumberFormat


fun formatAmountIndian(amount: Double?): String {
    // Handle null or invalid values
    if (amount == null || amount.isNaN() || amount.isInfinite()) {
        return "0.00"
    }

    // Ensure non-negative (if negative amounts need handling, remove abs() or adjust logic)
    val safeAmount = amount

    return try {
        val formatter: NumberFormat = DecimalFormat("#,##,##0.00")
        "${formatter.format(safeAmount)}"
    } catch (e: Exception) {
        e.printStackTrace()
        "0.00" // Fallback in case of formatting error
    }
}