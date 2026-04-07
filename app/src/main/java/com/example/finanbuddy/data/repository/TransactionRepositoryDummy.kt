package com.example.finanbuddy.data.repository

import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionTotals
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.TransactionRepository
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.abs

class TransactionRepositoryDummy : TransactionRepository {
    private val transactions = mutableListOf(
        Transaction(
            id = 1,
            type = TransactionType.INCOME,
            amount = 100.0,
            date = LocalDate.now(),
            time = LocalTime.now(),
            category = "Salary",
            note = "Monthly salary"
        ),
        Transaction(
            id = 2,
            type = TransactionType.EXPENSE,
            amount = -50.0,
            date = LocalDate.now().minusDays(1),
            time = LocalTime.now().minusHours(2),
            category = "food",
            note = "Chuche"
        )
    )

    override suspend fun saveTransaction(transaction: Transaction): Resource<Long> {
        val nextId = transactions.maxOfOrNull { it.id }?.plus(1) ?: 1
        val normalizedAmount = when (transaction.type) {
            TransactionType.INCOME -> abs(transaction.amount)
            TransactionType.EXPENSE -> -abs(transaction.amount)
        }

        val newTransaction = transaction.copy(id = nextId, amount = normalizedAmount)
        transactions.add(newTransaction)
        return Resource.Success(newTransaction.id)
    }

    override suspend fun getTransactions(): Resource<List<Transaction>> {
        return Resource.Success(transactions.sortedByDescending { it.date.atTime(it.time) })
    }

    override suspend fun getRecentTransactions(limit: Int): Resource<List<Transaction>> {
        val safeLimit = limit.coerceAtLeast(1)
        val recent = transactions
            .sortedByDescending { it.date.atTime(it.time) }
            .take(safeLimit)
        return Resource.Success(recent)
    }

    override suspend fun getTransactionsByType(type: TransactionType): Resource<List<Transaction>> {
        return Resource.Success(transactions.filter { it.type == type })
    }

    override suspend fun getTotalsSummary(): Resource<TransactionTotals> {
        val now = LocalDate.now()
        val currentMonth = now.month
        val currentYear = now.year
        val previousMonthDate = now.minusMonths(1)

        val monthIncomes = transactions
            .filter { it.type == TransactionType.INCOME }
            .filter { it.date.year == currentYear && it.date.month == currentMonth }
            .sumOf { it.amount }

        val monthExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .filter { it.date.year == currentYear && it.date.month == currentMonth }
            .sumOf { abs(it.amount) }

        val previousMonthIncomes = transactions
            .filter { it.type == TransactionType.INCOME }
            .filter {
                it.date.year == previousMonthDate.year &&
                    it.date.month == previousMonthDate.month
            }
            .sumOf { it.amount }

        val previousMonthExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .filter {
                it.date.year == previousMonthDate.year &&
                    it.date.month == previousMonthDate.month
            }
            .sumOf { abs(it.amount) }

        return Resource.Success(
            TransactionTotals(
                availableAmount = transactions.sumOf { it.amount },
                monthIncomes = monthIncomes,
                monthExpenses = monthExpenses,
                previousMonthIncomes = previousMonthIncomes,
                previousMonthExpenses = previousMonthExpenses
            )
        )
    }

    override suspend fun getTransactionsForMonthYear(
        month: Int,
        year: Int
    ): Resource<List<Transaction>> {
        return Resource.Success(
            transactions.filter { it.date.year == year && it.date.monthValue == month }
        )
    }

    override suspend fun getCurrentMonthTotal(type: TransactionType): Resource<Double> {
        return when (val totals = getTotalsSummary()) {
            is Resource.Success -> {
                val amount = when (type) {
                    TransactionType.INCOME -> totals.data.monthIncomes
                    TransactionType.EXPENSE -> totals.data.monthExpenses
                }
                Resource.Success(amount)
            }

            is Resource.Error -> totals
        }
    }
}

