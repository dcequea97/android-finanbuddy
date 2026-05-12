package com.example.finanbuddy.data.local.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.ui.graphics.Color

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val description: String,
    val type: String, // "INCOME" or "EXPENSE"
    val colorHex: String, // Stored as hex string (e.g., "0xFFEF4444")
    val iconName: String? = null // Optional: for potential future icon serialization
)

