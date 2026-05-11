package com.example.finanbuddy.data.remote.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseIdTokenProviderImpl(
    private val firebaseAuth: FirebaseAuth
) : FirebaseIdTokenProvider {

    override suspend fun getIdToken(forceRefresh: Boolean): String? {
        return try {
            firebaseAuth.currentUser
                ?.getIdToken(forceRefresh)
                ?.await()
                ?.token
                ?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}

