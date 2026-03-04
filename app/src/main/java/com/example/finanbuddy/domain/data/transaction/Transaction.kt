package com.example.finanbuddy.domain.data.transaction

import java.time.LocalDate
import java.time.LocalTime

data class Transaction(
    val id: Long,
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val time: LocalTime,
    val category: String,
    val note: String,
)