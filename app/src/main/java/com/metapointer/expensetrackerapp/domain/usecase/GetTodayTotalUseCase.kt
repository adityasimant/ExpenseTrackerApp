package com.metapointer.expensetrackerapp.domain.usecase

import com.metapointer.expensetrackerapp.domain.repository.ExpenseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTodayTotalUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(): Double {
        return repository.getTodayTotal()
    }
}