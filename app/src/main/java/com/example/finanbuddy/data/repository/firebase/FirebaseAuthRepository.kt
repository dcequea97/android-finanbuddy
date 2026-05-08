package com.example.finanbuddy.data.repository.firebase

import android.content.Context
import android.content.Intent
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context,
    private val googleWebClientId: String
) : AuthRepository {

    private val googleSignInClient by lazy {
        if (googleWebClientId.isBlank()) {
            null
        } else {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleWebClientId)
                .requestEmail()
                .build()
            GoogleSignIn.getClient(context, options)
        }
    }

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

    override fun buildGoogleSignInIntent(): Resource<Intent> {
        val client = googleSignInClient
            ?: return Resource.Error("Missing Google Web Client ID. Update google_web_client_id in strings.xml")

        client.signOut()
        return Resource.Success(client.signInIntent)
    }

    override fun extractGoogleIdToken(data: Intent?): Resource<String> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken.orEmpty()

            if (idToken.isBlank()) {
                Resource.Error("Google token is invalid")
            } else {
                Resource.Success(idToken)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google sign-in failed", e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}

