package com.example.finanbuddy.data.remote.model

import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import java.time.LocalDate
import java.time.LocalTime
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.util.Date

/**
 * Firestore-friendly shape using primitives/strings.
 */
data class FirestoreTransactionDto(
    val id: Long = 0,
    val type: String = TransactionType.EXPENSE.name,
    val amount: Double = 0.0,
    val date: String = Timestamp.now().toString(),
    val time: String = LocalTime.now().toString(),
    val category: String = "",
    val note: String = "",
)

fun Transaction.toFirestoreDto(id: Long = this.id, asTimestamp: Boolean = false): FirestoreTransactionDto {
    val dateTimestamp = if (asTimestamp) {
        Timestamp(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
    } else {
        Timestamp.now()
    }
    return FirestoreTransactionDto(
        id = id,
        type = type.name,
        amount = amount,
        date = date.toString(),
        time = time.toString(),
        category = category,
        note = note,
    )
}

fun FirestoreTransactionDto.toDomainOrNull(): Transaction? {
    val transactionType = TransactionType.entries.firstOrNull { it.name == type } ?: return null

    //convert the String in this format yyyy-mm-dd to LocalDate
    val localDate = LocalDate.parse(date)
    return try {
        Transaction(
            id = id,
            type = transactionType,
            amount = amount,
            date = localDate,
            time = LocalTime.parse(time),
            category = category,
            note = note,
        )
    } catch (_: Exception) {
        null
    }
}

