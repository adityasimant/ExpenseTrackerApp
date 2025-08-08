package com.metapointer.expensetrackerapp.di

import android.content.Context
import androidx.room.Room
import com.metapointer.expensetrackerapp.data.database.ExpenseDao
import com.metapointer.expensetrackerapp.data.database.ExpenseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideExpenseDatabase(@ApplicationContext context: Context): ExpenseDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = ExpenseDatabase::class.java,
            name = "expense_database"
        ).build()
    }

    @Provides
    fun provideExpenseDao(database: ExpenseDatabase): ExpenseDao {
        return database.expenseDao()
    }
}