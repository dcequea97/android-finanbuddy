package com.example.finanbuddy.domain.repository

import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import java.time.LocalDate
import java.time.LocalTime

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction): Resource<Long>
    suspend fun getTransactions(): Resource<List<Transaction>>
    suspend fun getTransactionsByType(type: TransactionType): Resource<List<Transaction>>
}

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
            amount = 50.0,
            date = LocalDate.now().minusDays(1),
            time = LocalTime.now().minusHours(2),
            category = "food",
            note = "Chuche"
        )
    )

    override suspend fun saveTransaction(transaction: Transaction): Resource<Long> {
        val nextId = transactions.maxOfOrNull { it.id }?.plus(1) ?: 1

        val newTransaction = transaction.copy(id = nextId)
        transactions.add(newTransaction)
        return Resource.Success(newTransaction.id)
    }

    override suspend fun getTransactions(): Resource<List<Transaction>> {
        return Resource.Success(transactions)
    }

    override suspend fun getTransactionsByType(type: TransactionType): Resource<List<Transaction>> {
        return Resource.Success(transactions.filter { it.type == type })
    }
}


