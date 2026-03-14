package com.example.finanbuddy.domain.repository

import android.content.Intent
import com.example.finanbuddy.domain.data.Resource

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    suspend fun signIn(email: String, password: String): Resource<Unit>
    suspend fun register(email: String, password: String): Resource<Unit>
    suspend fun signInWithGoogle(idToken: String): Resource<Unit>
    fun buildGoogleSignInIntent(): Resource<Intent>
    fun extractGoogleIdToken(data: Intent?): Resource<String>
    fun signOut()
}

