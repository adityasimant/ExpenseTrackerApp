package com.metapointer.expensetrackerapp.di

import com.metapointer.expensetrackerapp.data.repository.ExpenseRepositoryImpl
import com.metapointer.expensetrackerapp.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository
}