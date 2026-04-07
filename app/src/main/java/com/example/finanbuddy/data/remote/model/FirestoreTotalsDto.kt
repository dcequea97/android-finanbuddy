package com.example.finanbuddy.data.remote.model

/**
 * Aggregate totals document stored at transactions/{userId}/totals/summary.
 */
data class FirestoreTotalsDto(
    val availableAmount: Double = 0.0,
    val monthIncomes: Double = 0.0,
    val monthExpenses: Double = 0.0,
    val previousMonthIncomes: Double = 0.0,
    val previousMonthExpenses: Double = 0.0,
    val currentMonthKey: String = "",
    val previousMonthKey: String = "",
)


