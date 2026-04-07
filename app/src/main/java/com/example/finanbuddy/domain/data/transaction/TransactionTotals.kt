package com.example.finanbuddy.domain.data.transaction

data class TransactionTotals(
    val availableAmount: Double = 0.0,
    val monthIncomes: Double = 0.0,
    val monthExpenses: Double = 0.0,
    val previousMonthIncomes: Double = 0.0,
    val previousMonthExpenses: Double = 0.0,
)

