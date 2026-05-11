package com.example.finanbuddy.data.remote.model

import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.LocalTime
import java.time.ZoneId
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

    if (raw.isBlank()) return LocalDate.now()

    // 1. Intentar con los formatters conocidos
    for (formatter in dateFormatters) {
        runCatching { LocalDate.parse(raw, formatter) }
            .getOrNull()?.let { return it }
    }

    // 2. Intentar con formatos ISO estándar de forma secuencial
    val isoParsers = listOf(
        { OffsetDateTime.parse(raw).toLocalDate() },
        { LocalDateTime.parse(raw).toLocalDate() },
        { Instant.parse(raw).atZone(ZoneId.of("UTC")).toLocalDate() },
        { LocalDate.parse(raw) } // ISO_LOCAL_DATE por defecto
    )

    for (parser in isoParsers) {
        runCatching { parser() }
            .getOrNull()?.let { return it }
    }

    // 3. Log de advertencia si falló todo
    println("Advertencia: No se pudo parsear '$raw'. Usando fecha actual.")
    return LocalDate.now()
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