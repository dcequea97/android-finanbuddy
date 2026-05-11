package com.example.finanbuddy.domain.repository

import com.example.finanbuddy.domain.data.Resource

interface SettingsRepository {
    suspend fun getSheetsUrl(): Resource<String>
    suspend fun saveSheetsUrl(url: String): Resource<Unit>
}

