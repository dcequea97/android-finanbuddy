package com.example.finanbuddy.ui.screens.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.absoluteValue
class HomeViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private var hasLoadedInitialData = false
    private var monthTransactionsJob: Job? = null
    private var recentTransactionsJob: Job? = null
    private var totalsJob: Job? = null
    private var syncStatusJob: Job? = null
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()
        .onStart {
            if (!hasLoadedInitialData) {
                loadData()
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
        loadData()
        _state.update { it.copy(isRefreshing = false) }
    }
    private fun loadData() {
        monthTransactionsJob?.cancel()
        recentTransactionsJob?.cancel()
        totalsJob?.cancel()
        syncStatusJob?.cancel()

        _state.update {
            it.copy(
                isLoadingTransactions = true,
                errorMessage = null,
                transactionsIncomesByMonth = null,
                transactionsExpensesByMonth = null
            )
        }
        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue
        // Month chart data - via Flow (cache-first)
        monthTransactionsJob = viewModelScope.launch {
            transactionRepository.getTransactionsForMonthYearFlow(currentMonth, currentYear)
                .collect { transactions ->
                    _state.update {
                        it.copy(
                            transactionsIncomesByMonth = buildDailyTotals(transactions, currentYear, currentMonth, TransactionType.INCOME),
                            transactionsExpensesByMonth = buildDailyTotals(transactions, currentYear, currentMonth, TransactionType.EXPENSE),
                            isLoadingTransactions = false
                        )
                    }
                }
        }
        // Recent transactions list - via Flow (cache-first)
        recentTransactionsJob = viewModelScope.launch {
            transactionRepository.getRecentTransactionsFlow(RECENT_TRANSACTIONS_LIMIT)
                .collect { transactions ->
                    _state.update {
                        it.copy(transactions = transactions.sortedByDescending { tx -> tx.date }.take(RECENT_TRANSACTIONS_LIMIT))
                    }
                }
        }
        // Totals are now cache-first: Room shows last known value instantly.
        totalsJob = viewModelScope.launch {
            transactionRepository.getTotalsSummaryFlow().collect { totals ->
                _state.update {
                    it.copy(
                        availableBalance = totals.availableAmount,
                        totalMonthIncomeAmount = totals.monthIncomes,
                        totalMonthExpenseAmount = totals.monthExpenses,
                        differencePercentageIncome = calculateDifferencePercentage(
                            totals.monthIncomes,
                            totals.previousMonthIncomes
                        ),
                        differencePercentageExpense = calculateDifferencePercentage(
                            totals.monthExpenses,
                            totals.previousMonthExpenses
                        )
                    )
                }
            }
        }

        syncStatusJob = viewModelScope.launch {
            transactionRepository.isSyncingFromBackendFlow().collect { syncing ->
                _state.update { it.copy(isSyncingFromBackend = syncing) }
            }
        }
    }
    private fun buildDailyTotals(
        transactions: List<com.example.finanbuddy.domain.data.transaction.Transaction>,
        year: Int,
        month: Int,
        type: TransactionType
    ): List<Double> {
        val today = LocalDate.now()
        val isCurrentMonth = year == today.year && month == today.monthValue
        val limitDay = if (isCurrentMonth) today.dayOfMonth
                       else LocalDate.of(year, month, 1).lengthOfMonth()
        return (1..limitDay).map { day ->
            transactions
                .filter { it.type == type && it.date.dayOfMonth == day }
                .sumOf { it.amount }
                .absoluteValue
        }
    }
    private fun calculateDifferencePercentage(current: Double, previous: Double): Double {
        if (previous == 0.0) return if (current == 0.0) 0.0 else 100.0
        return ((current - previous) / previous) * 100
    }
    private companion object {
        const val RECENT_TRANSACTIONS_LIMIT = 5
    }
}
