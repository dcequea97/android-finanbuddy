package com.example.finanbuddy.data.local.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.finanbuddy.domain.data.transaction.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val type: String, // TransactionType serialized as string
    val amount: Double,
    val date: String, // Stored as "yyyy-MM-dd"
    val category: String,
    val note: String,
    val timestamp: Long // Timestamp for sorting
)

