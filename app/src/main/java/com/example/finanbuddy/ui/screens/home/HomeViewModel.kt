package com.example.finanbuddy.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.domain.data.onError
import com.example.finanbuddy.domain.data.onFinally
import com.example.finanbuddy.domain.data.onSuccess
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

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

	private fun loadRecentTransactions() {
		_state.update { it.copy(isLoading = true, errorMessage = null) }
		viewModelScope.launch {
			transactionRepository.getTransactions()
				.onSuccess { transactions ->
					val currentMonth = YearMonth.now()
					val previousMonth = currentMonth.minusMonths(1)

					val currentMonthTransactions = transactions.filter { tx ->
						YearMonth.from(tx.date) == currentMonth
					}

					val previousMonthTransactions = transactions.filter { tx ->
						YearMonth.from(tx.date) == previousMonth
					}

					val lastTwoMonthsTransactions = transactions.filter { tx ->
						val transactionMonth = YearMonth.from(tx.date)
						transactionMonth == currentMonth || transactionMonth == previousMonth
					}

					val totalLastTwoMonthsExpenses = lastTwoMonthsTransactions
						.filter { it.type == TransactionType.EXPENSE }
						.sumOf { it.amount }

					val totalLastTwoMonthsIncomes = lastTwoMonthsTransactions
						.filter { it.type == TransactionType.INCOME }
						.sumOf { it.amount }

					val currentMonthIncome = currentMonthTransactions
						.filter { it.type == TransactionType.INCOME }
						.sumOf { it.amount }

					val previousMonthIncome = previousMonthTransactions
						.filter { it.type == TransactionType.INCOME }
						.sumOf { it.amount }

					val currentMonthExpense = currentMonthTransactions
						.filter { it.type == TransactionType.EXPENSE }
						.sumOf { it.amount }

					val previousMonthExpense = previousMonthTransactions
						.filter { it.type == TransactionType.EXPENSE }
						.sumOf { it.amount }

					val incomeDifferencePercentage = calculateDifferencePercentage(
						current = currentMonthIncome,
						previous = previousMonthIncome
					)

					val expenseDifferencePercentage = calculateDifferencePercentage(
						current = currentMonthExpense,
						previous = previousMonthExpense
					)

					_state.update {
						it.copy(
							transactions = transactions
								.sortedByDescending { tx -> tx.date.atTime(tx.time) }
								.take(5),
							totalMonthExpenseAmount = totalLastTwoMonthsExpenses,
							totalMonthIncomeAmount = totalLastTwoMonthsIncomes,
							differencePercentageIncome = incomeDifferencePercentage,
							differencePercentageExpense = expenseDifferencePercentage
						)
					}
				}
				.onError { message ->
					_state.update { it.copy(errorMessage = message.ifBlank { "Could not load transactions" }) }
				}
				.onFinally {
					_state.update { it.copy(isLoading = false) }
				}
		}
	}

	private fun calculateDifferencePercentage(current: Double, previous: Double): Double {
		if (previous == 0.0) {
			return if (current == 0.0) 0.0 else 100.0
		}

		return ((current - previous) / previous) * 100
	}
}