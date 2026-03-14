package com.example.finanbuddy.domain.repository

import com.example.finanbuddy.domain.data.Resource

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    suspend fun signIn(email: String, password: String): Resource<Unit>
    suspend fun register(email: String, password: String): Resource<Unit>
    suspend fun signInWithGoogle(idToken: String): Resource<Unit>
    fun signOut()
}

