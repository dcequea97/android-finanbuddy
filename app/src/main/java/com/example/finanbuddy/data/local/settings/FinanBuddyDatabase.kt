package com.example.finanbuddy.data.local.settings

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.finanbuddy.data.local.transaction.TransactionEntity
import com.example.finanbuddy.data.local.transaction.TransactionDao
import com.example.finanbuddy.data.local.transaction.CategoryEntity
import com.example.finanbuddy.data.local.transaction.CategoryDao
import com.example.finanbuddy.data.local.transaction.TransactionTotalsEntity

@Database(
    entities = [
        SettingsEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        TransactionTotalsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class FinanBuddyDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
}

