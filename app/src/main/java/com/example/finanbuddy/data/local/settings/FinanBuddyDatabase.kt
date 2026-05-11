package com.example.finanbuddy.data.local.settings

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FinanBuddyDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}

