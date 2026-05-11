package com.example.finanbuddy.data.remote.network

interface FirebaseIdTokenProvider {
    suspend fun getIdToken(forceRefresh: Boolean = false): String?
}

