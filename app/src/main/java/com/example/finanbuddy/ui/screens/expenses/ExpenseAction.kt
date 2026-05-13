package com.example.finanbuddy.ui.screens.expenses

import java.time.LocalDate
import java.time.LocalTime

sealed interface ExpenseAction {
    data class InitializeEdit(
        val transactionId: Long,
        val categoryId: String,
        val amount: String,
        val dateIso: String,
        val note: String
    ) : ExpenseAction

    data class SetSelectedCategory(val categoryId: String) : ExpenseAction
    data class SetAmount(val amount: String) : ExpenseAction
    data class SetSelectedDate(val date: LocalDate) : ExpenseAction
    data class SetSelectedTime(val time: LocalTime) : ExpenseAction
    data class SetNote(val note: String) : ExpenseAction

    data object SaveTransaction : ExpenseAction
}