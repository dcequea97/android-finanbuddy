package com.example.finanbuddy.data.local.transaction

import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import java.time.LocalDate
import java.time.LocalTime

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        type = TransactionType.valueOf(type),
        amount = amount,
        date = LocalDate.parse(date),
        time = LocalTime.of(0, 0),
        category = category,
        note = note
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        type = type.name,
        amount = amount,
        date = date.toString(),
        category = category,
        note = note,
        timestamp = date.toEpochDay() * 86400000L
    )
}
