package com.example.finanbuddy.domain.repository

import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionTotals
import com.example.finanbuddy.domain.data.transaction.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction): Resource<Long>
    suspend fun updateTransaction(transaction: Transaction): Resource<Long>
    suspend fun getTransactions(): Resource<List<Transaction>>
    suspend fun getRecentTransactions(limit: Int): Resource<List<Transaction>>
    suspend fun getTransactionsByType(type: TransactionType): Resource<List<Transaction>>
    suspend fun getTotalsSummary(): Resource<TransactionTotals>
    suspend fun getTransactionsForMonthYear(month: Int, year: Int): Resource<List<Transaction>>
    suspend fun getCurrentMonthTotal(type: TransactionType): Resource<Double>

    /**
     * Flow-based methods: emit cached data immediately, then update when remote syncs.
     * Default implementations wrap the suspend methods for non-cached repositories.
     */
    fun getTransactionsFlow(): Flow<List<Transaction>> = flowOf(emptyList())
    fun getRecentTransactionsFlow(limit: Int): Flow<List<Transaction>> = flowOf(emptyList())
    fun getTransactionsForMonthYearFlow(month: Int, year: Int): Flow<List<Transaction>> = flowOf(emptyList())
    fun getTotalsSummaryFlow(): Flow<TransactionTotals> = flowOf(TransactionTotals())
    fun isSyncingFromBackendFlow(): Flow<Boolean> = flowOf(false)
}





