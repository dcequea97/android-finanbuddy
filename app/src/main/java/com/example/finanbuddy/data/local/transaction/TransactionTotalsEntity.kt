package com.example.finanbuddy.data.local.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_totals")
data class TransactionTotalsEntity(
    @PrimaryKey
    val id: Int = SINGLE_ROW_ID,
    val availableAmount: Double,
    val monthIncomes: Double,
    val monthExpenses: Double,
    val previousMonthIncomes: Double,
    val previousMonthExpenses: Double,
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    companion object {
        const val SINGLE_ROW_ID = 1
    }
}

