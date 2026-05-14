package com.example.finanbuddy.domain.data.transaction

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@Serializable
data class TransactionAiModel(
    val business: String,
    val date: String,
    val amount: Double,
    val category: String,
    val notes: List<ProductNote>
)

@Serializable
data class ProductNote(
    val product: String,
    val price: Int
)

fun TransactionAiModel.toDomain(): Transaction {
    return Transaction(
        id = 0,
        type = TransactionType.EXPENSE,
        date = Instant.parse(date).atZone(ZoneOffset.UTC).toLocalDate(),
        time = LocalTime.of(0, 0),
        amount = amount / 3550,
        category = category,
        note = "$business -> ${notes.joinToString(", ") { "${ it.product }: ${ it.price }" }}"
    )
}