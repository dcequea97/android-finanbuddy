package com.example.finanbuddy.data.remote.model

import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import kotlinx.serialization.Serializable
import java.time.LocalDate
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
    for (formatter in dateFormatters) {
        return try {
            LocalDate.parse(dateString.trim(), formatter)
        } catch (e: Exception) {
            continue
        }
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