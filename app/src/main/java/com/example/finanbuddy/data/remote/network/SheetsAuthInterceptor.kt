package com.example.finanbuddy.data.remote.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class SheetsAuthInterceptor(
    private val idTokenProvider: FirebaseIdTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!request.header("Authorization").isNullOrBlank()) {
            return chain.proceed(request)
        }

        val token = runBlocking(Dispatchers.IO) {
            idTokenProvider.getIdToken(forceRefresh = false)
        }

        if (token.isNullOrBlank()) {
            return chain.proceed(request)
        }

        val authenticatedRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}

