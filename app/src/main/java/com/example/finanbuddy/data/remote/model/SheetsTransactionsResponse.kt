package com.example.finanbuddy.data.remote.model

import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SheetsTransactionsResponse : ArrayList<SheetsTransactionsResponseItem>()

@Serializable
data class SheetsTransactionsResponseItem(
    val categoria: String,
    val concepto: String,
    val fecha: String,
    val monto: Double
)

private val dateFormatters = listOf(
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    DateTimeFormatter.ofPattern("dd-MM-yyyy"),
    DateTimeFormatter.ofPattern("MM/dd/yyyy"),
    DateTimeFormatter.ofPattern("yyyy/MM/dd")
)

fun parseDateFlexible(dateString: String): LocalDate {
    val raw = dateString.trim()

    for (formatter in dateFormatters) {
        try {
            return LocalDate.parse(raw, formatter)
        } catch (_: Exception) {
            // Keep trying next known format.
        }
    }

    // Accept ISO date-time payloads like 2026-05-08T00:00:00.000Z
    try {
        return OffsetDateTime.parse(raw, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate()
    } catch (_: Exception) {
    }

    try {
        return Instant.parse(raw).atOffset(java.time.ZoneOffset.UTC).toLocalDate()
    } catch (_: Exception) {
    }

    try {
        return LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate()
    } catch (_: Exception) {
    }

    throw IllegalArgumentException("Unable to parse date: $dateString")
}

fun SheetsTransactionsResponseItem.toDomain(): Transaction {
    return Transaction(
        id = 0L,
        category = categoria,
        note = concepto,
        date = parseDateFlexible(fecha),
        amount = monto,
        type = TransactionType.EXPENSE,
        time = LocalTime.of(0, 0, 0)
    )
}