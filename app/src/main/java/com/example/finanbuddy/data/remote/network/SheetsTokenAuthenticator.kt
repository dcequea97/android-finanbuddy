package com.example.finanbuddy.data.remote.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class SheetsTokenAuthenticator(
	private val idTokenProvider: FirebaseIdTokenProvider
) : Authenticator {

	override fun authenticate(route: Route?, response: Response): Request? {
		if (responseCount(response) >= 2) return null

		val refreshedToken = runBlocking(Dispatchers.IO) {
			idTokenProvider.getIdToken(forceRefresh = true)
		}

		if (refreshedToken.isNullOrBlank()) return null

		val updatedHeader = "Bearer $refreshedToken"
		if (response.request.header("Authorization") == updatedHeader) {
			return null
		}

		return response.request.newBuilder()
			.header("Authorization", updatedHeader)
			.build()
	}

	private fun responseCount(response: Response): Int {
		var count = 1
		var priorResponse = response.priorResponse
		while (priorResponse != null) {
			count++
			priorResponse = priorResponse.priorResponse
		}
		return count
	}
}

