package com.example.finanbuddy.data.repository.local

import com.example.finanbuddy.data.local.settings.SettingsDao
import com.example.finanbuddy.data.local.settings.SettingsEntity
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.repository.SettingsRepository

class RoomSettingsRepository(
    private val settingsDao: SettingsDao
) : SettingsRepository {

    override suspend fun getSheetsUrl(): Resource<String> {
        return try {
            val savedSettings = settingsDao.getById()
            Resource.Success(savedSettings?.sheetsUrl.orEmpty())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to load settings", e)
        }
    }

    override suspend fun saveSheetsUrl(url: String): Resource<Unit> {
        return try {
            settingsDao.upsert(
                SettingsEntity(
                    id = SettingsEntity.SINGLETON_ID,
                    sheetsUrl = url
                )
            )
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to save settings", e)
        }
    }
}

