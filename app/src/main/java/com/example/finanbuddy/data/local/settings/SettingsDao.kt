package com.example.finanbuddy.data.local.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int = SettingsEntity.SINGLETON_ID): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: SettingsEntity)
}

