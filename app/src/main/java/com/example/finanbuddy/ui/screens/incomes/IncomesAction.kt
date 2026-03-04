package com.example.finanbuddy.ui.screens.incomes

import com.example.finanbuddy.ui.screens.expenses.ExpenseAction
import java.time.LocalDate
import java.time.LocalTime

sealed interface IncomesAction {
    data class SetSelectedCategory(val categoryId: String) : IncomesAction
    data class SetAmount(val amount: String) : IncomesAction
    data class SetSelectedDate(val date: LocalDate) : IncomesAction
    data class SetSelectedTime(val time: LocalTime) : IncomesAction
    data object SaveTransaction: IncomesAction
}