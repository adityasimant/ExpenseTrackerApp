package com.metapointer.expensetrackerapp.ui.screens.entry.model

data class FormValidationState(
    val titleValidation: ValidationResult = ValidationResult(true),
    val amountValidation: ValidationResult = ValidationResult(true),
    val categoryValidation: ValidationResult = ValidationResult(true),
    val notesValidation: ValidationResult = ValidationResult(true),
    val isFormValid: Boolean = false
)