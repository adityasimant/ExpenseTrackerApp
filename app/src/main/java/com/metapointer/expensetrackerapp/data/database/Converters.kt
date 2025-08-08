package com.metapointer.expensetrackerapp.data.database

import androidx.room.TypeConverter
import com.metapointer.expensetrackerapp.data.model.ExpenseCategory

class Converters {
    @TypeConverter
    fun fromExpenseCategory(category: ExpenseCategory): String {
        return category.name
    }

    @TypeConverter
    fun toExpenseCategory(categoryName: String): ExpenseCategory {
        return ExpenseCategory.valueOf(categoryName)
    }
}