package com.example.finanbuddy.data.repository

import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override suspend fun signIn(email: String, password: String): Resource<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to sign in", e)
        }
    }

    override suspend fun register(email: String, password: String): Resource<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to register", e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Resource<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to sign in with Google", e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}

