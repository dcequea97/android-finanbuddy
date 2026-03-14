package com.example.finanbuddy.data.repository

import com.example.finanbuddy.data.remote.model.FirestoreTransactionDto
import com.example.finanbuddy.data.remote.model.toDomainOrNull
import com.example.finanbuddy.data.remote.model.toFirestoreDto
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.TransactionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirebaseTransactionRepository(
    firestore: FirebaseFirestore
) : TransactionRepository {

    private val transactionsCollection = firestore.collection(TRANSACTIONS_COLLECTION)

    override suspend fun saveTransaction(transaction: Transaction): Resource<Long> {
        return try {
            val docRef = transactionsCollection.document()
            val id = documentIdToLong(docRef.id)

            docRef
                .set(transaction.toFirestoreDto(id = id))
                .await()

            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Unable to save transaction",
                cause = e
            )
        }
    }

    override suspend fun getTransactions(): Resource<List<Transaction>> {
        return try {
            val transactions = transactionsCollection
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    val dto = document.toObject(FirestoreTransactionDto::class.java) ?: return@mapNotNull null
                    val tx = dto.toDomainOrNull() ?: return@mapNotNull null
                    if (tx.id > 0L) tx else tx.copy(id = documentIdToLong(document.id))
                }
                .sortedByDescending { tx -> tx.date.atTime(tx.time) }

            Resource.Success(transactions)
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Unable to load transactions",
                cause = e
            )
        }
    }

    override suspend fun getTransactionsByType(type: TransactionType): Resource<List<Transaction>> {
        return when (val result = getTransactions()) {
            is Resource.Success -> Resource.Success(result.data.filter { it.type == type })
            is Resource.Error -> result
        }
    }

    override suspend fun getCurrentMonthTotal(type: TransactionType): Resource<Double> {
        val now = LocalDate.now()

        return when (val result = getTransactionsByType(type)) {
            is Resource.Success -> {
                val total = result.data
                    .asSequence()
                    .filter { it.date.year == now.year && it.date.month == now.month }
                    .sumOf { it.amount }

                Resource.Success(total)
            }
            is Resource.Error -> result
        }
    }

    private fun documentIdToLong(documentId: String): Long {
        val value = documentId.hashCode().toLong() and Long.MAX_VALUE
        return if (value == 0L) 1L else value
    }

    private companion object {
        const val TRANSACTIONS_COLLECTION = "transactions"
    }
}



