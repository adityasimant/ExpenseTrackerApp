package com.metapointer.expensetrackerapp.ui.screens.entry.model

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)