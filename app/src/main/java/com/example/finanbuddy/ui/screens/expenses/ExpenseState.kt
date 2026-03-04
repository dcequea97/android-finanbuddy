package com.example.finanbuddy.ui.screens.expenses

import com.example.finanbuddy.domain.data.expense.CategoryModel
import java.time.LocalDate
import java.time.LocalTime

data class ExpenseState(
    val isLoading: Boolean = false,
    val selectedCategory: String? = "",
    val amount: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val note: String = "",
    val categories: List<CategoryModel> = listOf()
)