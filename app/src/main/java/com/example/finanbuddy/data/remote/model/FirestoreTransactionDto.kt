package com.example.finanbuddy.data.remote.model

import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import java.time.LocalDate
import java.time.LocalTime

/**
 * Firestore-friendly shape using primitives/strings.
 */
data class FirestoreTransactionDto(
    val id: Long = 0,
    val type: String = TransactionType.EXPENSE.name,
    val amount: Double = 0.0,
    val date: String = LocalDate.now().toString(),
    val time: String = LocalTime.now().toString(),
    val category: String = "",
    val note: String = ""
)

fun Transaction.toFirestoreDto(id: Long = this.id): FirestoreTransactionDto {
    return FirestoreTransactionDto(
        id = id,
        type = type.name,
        amount = amount,
        date = date.toString(),
        time = time.toString(),
        category = category,
        note = note
    )
}

fun FirestoreTransactionDto.toDomainOrNull(): Transaction? {
    val transactionType = TransactionType.entries.firstOrNull { it.name == type } ?: return null
    return try {
        Transaction(
            id = id,
            type = transactionType,
            amount = amount,
            date = LocalDate.parse(date),
            time = LocalTime.parse(time),
            category = category,
            note = note
        )
    } catch (_: Exception) {
        null
    }
}

