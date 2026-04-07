package com.example.finanbuddy.ui.screens.home

import com.example.finanbuddy.domain.data.transaction.Transaction

data class HomeState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null,

    val totalMonthExpenseAmount: Double = 0.0,
    val differencePercentageExpense: Double = 0.0,
    val totalMonthIncomeAmount: Double = 0.0,
    val differencePercentageIncome: Double = 0.0,
    val availableBalance: Double = 0.0,
    val transactionsIncomesByMonth: List<Double>? = null,
    val transactionsExpensesByMonth: List<Double>? = null,

    val isLoadingTransactions: Boolean = true
)