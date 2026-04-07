package com.example.finanbuddy.domain.repository

import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionTotals
import com.example.finanbuddy.domain.data.transaction.TransactionType

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction): Resource<Long>
    suspend fun getTransactions(): Resource<List<Transaction>>
    suspend fun getRecentTransactions(limit: Int): Resource<List<Transaction>>
    suspend fun getTransactionsByType(type: TransactionType): Resource<List<Transaction>>
    suspend fun getTotalsSummary(): Resource<TransactionTotals>

    suspend fun getTransactionsForMonthYear(month: Int, year: Int): Resource<List<Transaction>>

    /**
     * Returns the total amount for the current device month and year.
     * Use INCOME for earned total and EXPENSE for spent total.
     */
    suspend fun getCurrentMonthTotal(type: TransactionType): Resource<Double>
}



