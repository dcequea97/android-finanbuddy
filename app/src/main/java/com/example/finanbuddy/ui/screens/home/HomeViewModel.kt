package com.example.finanbuddy.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.onSuccess
import com.example.finanbuddy.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.absoluteValue

class HomeViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()
        .onStart {
            if (!hasLoadedInitialData) {
                loadRecentTransactions()
                hasLoadedInitialData = true
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnRefresh -> refresh()
        }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadRecentTransactions()
        _state.update { it.copy(isRefreshing = false) }
    }


    private fun loadRecentTransactions() {
        _state.update {
            it.copy(
                isLoadingTransactions = true,
                errorMessage = null,
                transactionsIncomesByMonth = null,
                transactionsExpensesByMonth = null
            )
        }
        viewModelScope.launch {
            val currentYearInt = LocalDate.now().year
            val currentMonthInt = LocalDate.now().monthValue

            transactionRepository.getTransactionsForMonthYear(currentMonthInt, currentYearInt)
                .onSuccess { transactions ->
                    val totalIncomes = mutableListOf<Double>()
                    val totalExpenses = mutableListOf<Double>()

                    val today = LocalDate.now()
                    val isCurrentMonth =
                        currentYearInt == today.year && currentMonthInt == today.monthValue

                    val limitDay = if (isCurrentMonth) {
                        today.dayOfMonth
                    } else {
                        LocalDate.of(currentYearInt, currentMonthInt, 1).lengthOfMonth()
                    }

                    for (i in 1..limitDay) {
                        val dayIncomes =
                            transactions.filter { it.type == com.example.finanbuddy.domain.data.transaction.TransactionType.INCOME }
                                .filter { it.date.dayOfMonth == i }
                                .sumOf { it.amount }

                        val dayExpenses =
                            transactions.filter { it.type == com.example.finanbuddy.domain.data.transaction.TransactionType.EXPENSE }
                                .filter { it.date.dayOfMonth == i }
                                .sumOf { it.amount }
                                .absoluteValue

                        totalIncomes.add(dayIncomes)
                        totalExpenses.add(dayExpenses)
                    }

                    _state.update {
                        it.copy(
                            transactionsIncomesByMonth = totalIncomes,
                            transactionsExpensesByMonth = totalExpenses
                        )
                    }
                }

            val transactionsResult =
                transactionRepository.getRecentTransactions(RECENT_TRANSACTIONS_LIMIT)
            val totalsResult = transactionRepository.getTotalsSummary()

            var errorMessage: String? = null

            when (transactionsResult) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            transactions = transactionsResult.data
                                .sortedByDescending { tx -> tx.date }
                                .take(5)
                        )
                    }
                }

                is Resource.Error -> {
                    errorMessage =
                        transactionsResult.message.ifBlank { "Could not load transactions" }
                }
            }

            when (totalsResult) {
                is Resource.Success -> {
                    val totals = totalsResult.data
                    val totalLastTwoMonthsIncomes =
                        totals.monthIncomes + totals.previousMonthIncomes
                    val totalLastTwoMonthsExpenses =
                        totals.monthExpenses + totals.previousMonthExpenses

                    _state.update {
                        it.copy(
                            totalMonthExpenseAmount = totalLastTwoMonthsExpenses,
                            totalMonthIncomeAmount = totalLastTwoMonthsIncomes,
                            differencePercentageIncome = calculateDifferencePercentage(
                                current = totals.monthIncomes,
                                previous = totals.previousMonthIncomes
                            ),
                            differencePercentageExpense = calculateDifferencePercentage(
                                current = totals.monthExpenses,
                                previous = totals.previousMonthExpenses
                            ),
                            availableBalance = totals.availableAmount
                        )
                    }
                }

                is Resource.Error -> {
                    if (errorMessage == null) {
                        errorMessage = totalsResult.message.ifBlank { "Could not load totals" }
                    }
                }
            }

            _state.update {
                it.copy(
                    errorMessage = errorMessage,
                    isLoadingTransactions = false
                )
            }
        }
    }

    private fun calculateDifferencePercentage(current: Double, previous: Double): Double {
        if (previous == 0.0) {
            return if (current == 0.0) 0.0 else 100.0
        }

        return ((current - previous) / previous) * 100
    }

    private companion object {
        const val RECENT_TRANSACTIONS_LIMIT = 5
    }
}