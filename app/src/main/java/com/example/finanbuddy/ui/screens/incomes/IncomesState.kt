package com.example.finanbuddy.ui.screens.incomes

import com.example.finanbuddy.domain.data.expense.CategoryModel
import java.time.LocalDate
import java.time.LocalTime

data class IncomesState(
    val isLoading: Boolean = false,
    val amount: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val selectedCategory: String? = "",
    val categories: List<CategoryModel> = listOf()
)