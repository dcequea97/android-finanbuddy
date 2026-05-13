package com.example.finanbuddy.data.repository.cache

import com.example.finanbuddy.data.local.transaction.TransactionDao
import com.example.finanbuddy.data.local.transaction.toDomain
import com.example.finanbuddy.data.local.transaction.toEntity
import com.example.finanbuddy.data.local.transaction.toEntity as totalsToEntity
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionTotals
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Cache-first repository:
 * - Immediately returns data from Room via Flow (observed by UI)
 * - Launches background sync from remote in externalScope
 * - When remote responds, updates Room → UI refreshes automatically via Flow
 */
class CachedTransactionRepository(
    private val remoteRepository: TransactionRepository,
    private val transactionDao: TransactionDao,
    private val externalScope: CoroutineScope
) : TransactionRepository {

    private val isSyncingFromBackend = MutableStateFlow(false)

    override suspend fun saveTransaction(transaction: Transaction): Resource<Long> {
        return remoteRepository.saveTransaction(transaction).also { result ->
            if (result is Resource.Success) {
                transactionDao.insertTransaction(transaction.toEntity())
            }
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Resource<Long> {
        return remoteRepository.updateTransaction(transaction).also { result ->
            if (result is Resource.Success) {
                transactionDao.insertTransaction(transaction.toEntity())
            }
        }
    }

    override suspend fun getTransactions(): Resource<List<Transaction>> = Resource.Success(emptyList())

    override fun getTransactionsFlow(): Flow<List<Transaction>> {
        externalScope.launch {
            when (val result = remoteRepository.getTransactions()) {
                is Resource.Success -> {
                    val entities = result.data.map { it.toEntity() }
                    transactionDao.insertTransactions(entities)
                    if (entities.isNotEmpty()) {
                        transactionDao.deleteNotIn(entities.map { it.id })
                    }
                }
                else -> {}
            }
        }
        return transactionDao.getAllTransactionsFlow()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getRecentTransactions(limit: Int): Resource<List<Transaction>> = Resource.Success(emptyList())

    override fun getRecentTransactionsFlow(limit: Int): Flow<List<Transaction>> {
        externalScope.launch {
            when (val result = remoteRepository.getTransactions()) {
                is Resource.Success -> {
                    val entities = result.data.map { it.toEntity() }
                    transactionDao.insertTransactions(entities)
                    if (entities.isNotEmpty()) {
                        transactionDao.deleteNotIn(entities.map { it.id })
                    }
                }
                else -> {}
            }
        }
        return transactionDao.getRecentTransactionsFlow(limit)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getTransactionsByType(type: TransactionType): Resource<List<Transaction>> = Resource.Success(emptyList())

    override suspend fun getTotalsSummary(): Resource<TransactionTotals> {
        isSyncingFromBackend.value = true
        return try {
            when (val result = remoteRepository.getTotalsSummary()) {
                is Resource.Success -> {
                    transactionDao.upsertTotals(result.data.totalsToEntity())
                    result
                }
                is Resource.Error -> result
            }
        } finally {
            isSyncingFromBackend.value = false
        }
    }

    override suspend fun getTransactionsForMonthYear(month: Int, year: Int): Resource<List<Transaction>> = Resource.Success(emptyList())

    override fun getTransactionsForMonthYearFlow(month: Int, year: Int): Flow<List<Transaction>> {
        externalScope.launch {
            when (val result = remoteRepository.getTransactionsForMonthYear(month, year)) {
                is Resource.Success -> {
                    val entities = result.data.map { it.toEntity() }
                    transactionDao.insertTransactions(entities)
                    if (entities.isNotEmpty()) {
                        transactionDao.deleteNotIn(entities.map { it.id })
                    }
                }
                else -> {}
            }
        }
        return transactionDao.getAllTransactionsFlow()
            .map { entities ->
                entities.map { it.toDomain() }
                    .filter { it.date.year == year && it.date.monthValue == month }
            }
    }

    override suspend fun getCurrentMonthTotal(type: TransactionType): Resource<Double> =
        remoteRepository.getCurrentMonthTotal(type)

    override fun getTotalsSummaryFlow(): Flow<TransactionTotals> {
        externalScope.launch {
            getTotalsSummary()
        }
        return transactionDao.getTotalsFlow()
            .map { it?.toDomain() ?: TransactionTotals() }
    }

    override fun isSyncingFromBackendFlow(): Flow<Boolean> {
        return isSyncingFromBackend.asStateFlow()
    }
}

