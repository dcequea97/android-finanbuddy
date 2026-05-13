package com.example.finanbuddy.data.repository.firebase

import com.example.finanbuddy.data.remote.model.FirestoreTotalsDto
import com.example.finanbuddy.data.remote.model.toFirestoreDto
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionTotals
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.abs

class FirebaseTransactionRepository(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : TransactionRepository {

    override suspend fun saveTransaction(transaction: Transaction): Resource<Long> {
        val userId = firebaseAuth.currentUser?.uid
            ?: return Resource.Error(message = "No authenticated user found")

        return try {
            val normalizedAmount = normalizeAmount(transaction.type, transaction.amount)
            val normalizedTransaction = transaction.copy(amount = normalizedAmount)

            val transactionDocRef = userTransactionsCollection(userId).document()
            val transactionId = documentIdToLong(transactionDocRef.id)

            val currentMonth = YearMonth.now()
            val previousMonth = currentMonth.minusMonths(1)
            val transactionMonth = YearMonth.from(normalizedTransaction.date)
            val currentMonthKey = currentMonth.toString()
            val previousMonthKey = previousMonth.toString()
            val transactionMonthKey = transactionMonth.toString()
            val amountAbs = abs(normalizedAmount)

            firestore.runTransaction { tx ->
                val userTransactionsRef = transactionsCollection.document(userId)
                val totalsRef = userTotalsDocument(userId)
                val totalsSnapshot = tx.get(totalsRef)
                val currentTotals = totalsSnapshot.toObject(FirestoreTotalsDto::class.java)
                    ?: FirestoreTotalsDto()
                val totalsWindow = normalizeTotalsWindow(
                    totals = currentTotals,
                    currentMonthKey = currentMonthKey,
                    previousMonthKey = previousMonthKey
                )

                val updatedTotals = totalsWindow.copy(
                    availableAmount = totalsWindow.availableAmount + normalizedAmount,
                    monthIncomes = totalsWindow.monthIncomes + if (
                        normalizedTransaction.type == TransactionType.INCOME && transactionMonthKey == currentMonthKey
                    ) {
                        amountAbs
                    } else {
                        0.0
                    },
                    monthExpenses = totalsWindow.monthExpenses + if (
                        normalizedTransaction.type == TransactionType.EXPENSE && transactionMonthKey == currentMonthKey
                    ) {
                        amountAbs
                    } else {
                        0.0
                    },
                    previousMonthIncomes = totalsWindow.previousMonthIncomes + if (
                        normalizedTransaction.type == TransactionType.INCOME && transactionMonthKey == previousMonthKey
                    ) {
                        amountAbs
                    } else {
                        0.0
                    },
                    previousMonthExpenses = totalsWindow.previousMonthExpenses + if (
                        normalizedTransaction.type == TransactionType.EXPENSE && transactionMonthKey == previousMonthKey
                    ) {
                        amountAbs
                    } else {
                        0.0
                    },
                    currentMonthKey = currentMonthKey,
                    previousMonthKey = previousMonthKey
                )

                // Keep a visible parent document in Firestore console for this user's transactions.
                tx.set(
                    userTransactionsRef,
                    mapOf(
                        "updatedAt" to FieldValue.serverTimestamp(),
                        "transactionCount" to FieldValue.increment(1)
                    ),
                    SetOptions.merge()
                )
                // First write the transaction record.
                    // Ensure date is stored as a Firestore Timestamp in the DTO
                    tx.set(transactionDocRef, normalizedTransaction.toFirestoreDto(id = transactionId, asTimestamp = false))
                // Then update aggregate totals in the same atomic transaction.
                tx.set(totalsRef, updatedTotals)
                null
            }.await()

            Resource.Success(transactionId)
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Unable to save transaction",
                cause = e
            )
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): Resource<Long> {
        return Resource.Error(message = "Update transaction not implemented for Firebase repository")
    }

    override suspend fun getTransactions(): Resource<List<Transaction>> {
        val userId = firebaseAuth.currentUser?.uid
            ?: return Resource.Error(message = "No authenticated user found")

        return try {
            val transactions = userTransactionsCollection(userId)
                .get()
                .await()
                .documents
                .mapNotNull(::toDomainTransactionOrNull)
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

    override suspend fun getRecentTransactions(limit: Int): Resource<List<Transaction>> {
        val userId = firebaseAuth.currentUser?.uid
            ?: return Resource.Error(message = "No authenticated user found")
        val safeLimit = limit.coerceAtLeast(1)

        return try {
            val transactions = userTransactionsCollection(userId)
                .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
                .orderBy(FIELD_TIME, Query.Direction.DESCENDING)
                .limit(safeLimit.toLong())
                .get()
                .await()
                .documents
                .mapNotNull(::toDomainTransactionOrNull)

            Resource.Success(transactions)
        } catch (_: Exception) {
            when (val allTransactions = getTransactions()) {
                is Resource.Success -> Resource.Success(allTransactions.data.take(safeLimit))
                is Resource.Error -> allTransactions
            }
        }
    }

    override suspend fun getTotalsSummary(): Resource<TransactionTotals> {
        val userId = firebaseAuth.currentUser?.uid
            ?: return Resource.Error(message = "No authenticated user found")

        return try {
            val snapshot = userTotalsDocument(userId).get().await()
            val dto = snapshot.toObject(FirestoreTotalsDto::class.java) ?: FirestoreTotalsDto()

            Resource.Success(
                TransactionTotals(
                    availableAmount = dto.availableAmount,
                    monthIncomes = dto.monthIncomes,
                    monthExpenses = dto.monthExpenses,
                    previousMonthIncomes = dto.previousMonthIncomes,
                    previousMonthExpenses = dto.previousMonthExpenses
                )
            )
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Unable to load totals summary",
                cause = e
            )
        }
    }

    override suspend fun getTransactionsForMonthYear(
        month: Int,
        year: Int
    ): Resource<List<Transaction>> {
        // get the transactions from the database and filter by month and year
        val userId = firebaseAuth.currentUser?.uid
            ?: return Resource.Error(message = "No authenticated user found")

        return try {

            val startLocalDate = LocalDate.of(year, month, 1)
            val endLocalDate = startLocalDate.plusMonths(1)

            val startDateString = startLocalDate.toString() // yyyy-MM-dd
            val endDateString = endLocalDate.toString() // yyyy-MM-dd

            val transactions = userTransactionsCollection(userId)
                .whereGreaterThanOrEqualTo("date", startDateString)
                .whereLessThan("date", endDateString)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull(::toDomainTransactionOrNull)

            return Resource.Success(transactions)
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Unable to load transactions",
                cause = e
            )
        }
    }

    override suspend fun getCurrentMonthTotal(type: TransactionType): Resource<Double> {
        return when (val result = getTotalsSummary()) {
            is Resource.Success -> {
                val total = when (type) {
                    TransactionType.INCOME -> result.data.monthIncomes
                    TransactionType.EXPENSE -> result.data.monthExpenses
                }
                Resource.Success(total)
            }

            is Resource.Error -> result
        }
    }

    private fun documentIdToLong(documentId: String): Long {
        val value = documentId.hashCode().toLong() and Long.MAX_VALUE
        return if (value == 0L) 1L else value
    }

    private fun toDomainTransactionOrNull(document: DocumentSnapshot): Transaction? {
        // Read 'date' as String, not Timestamp
        val id = document.getLong("id") ?: 0L
        val type = document.getString("type") ?: TransactionType.EXPENSE.name
        val amount = document.getDouble("amount") ?: 0.0
        val dateString = document.getString("date") ?: return null
        val category = document.getString("category") ?: ""
        val note = document.getString("note") ?: ""
        return try {
            Transaction(
                id = id,
                type = TransactionType.valueOf(type),
                amount = amount,
                date = LocalDate.parse(dateString),
                time = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toLocalTime(), // Default to midnight if time is missing
                category = category,
                note = note
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun normalizeTotalsWindow(
        totals: FirestoreTotalsDto,
        currentMonthKey: String,
        previousMonthKey: String
    ): FirestoreTotalsDto {
        val isCurrentWindow =
            totals.currentMonthKey == currentMonthKey && totals.previousMonthKey == previousMonthKey
        if (isCurrentWindow) {
            return totals
        }

        val shouldShiftCurrentToPrevious = totals.currentMonthKey == previousMonthKey
        return if (shouldShiftCurrentToPrevious) {
            totals.copy(
                monthIncomes = 0.0,
                monthExpenses = 0.0,
                previousMonthIncomes = totals.monthIncomes,
                previousMonthExpenses = totals.monthExpenses,
                currentMonthKey = currentMonthKey,
                previousMonthKey = previousMonthKey
            )
        } else {
            totals.copy(
                monthIncomes = 0.0,
                monthExpenses = 0.0,
                previousMonthIncomes = 0.0,
                previousMonthExpenses = 0.0,
                currentMonthKey = currentMonthKey,
                previousMonthKey = previousMonthKey
            )
        }
    }

    private fun userTransactionsCollection(userId: String) =
        transactionsCollection.document(userId).collection(USER_TRANSACTIONS_COLLECTION)

    private fun userTotalsDocument(userId: String) =
        transactionsCollection
            .document(userId)
            .collection(TOTALS_COLLECTION)
            .document(USER_TOTALS_DOCUMENT)

    private fun normalizeAmount(type: TransactionType, amount: Double): Double {
        val absoluteAmount = abs(amount)
        return when (type) {
            TransactionType.EXPENSE -> -absoluteAmount
            TransactionType.INCOME -> absoluteAmount
        }
    }

    private companion object {
        const val TRANSACTIONS_COLLECTION = "transactions"
        const val USER_TRANSACTIONS_COLLECTION = "items"
        const val TOTALS_COLLECTION = "totals"
        const val USER_TOTALS_DOCUMENT = "summary"
        const val FIELD_DATE = "date"
        const val FIELD_TIME = "time"
    }

    private val transactionsCollection = firestore.collection(TRANSACTIONS_COLLECTION)
}
