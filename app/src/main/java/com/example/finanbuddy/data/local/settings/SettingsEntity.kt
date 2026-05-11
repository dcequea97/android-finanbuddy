package com.example.finanbuddy.data.local.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = SINGLETON_ID,
    val sheetsUrl: String
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}

