package com.example.finanbuddy.data.remote.network

import com.example.finanbuddy.BuildConfig
import com.example.finanbuddy.data.local.settings.SettingsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Rewrites requests created with BuildConfig.API_BASE_URL to a URL saved in Room at runtime.
 */
class SheetsBaseUrlInterceptor(
    private val settingsDao: SettingsDao
) : Interceptor {

    private val defaultBaseUrl: HttpUrl? = BuildConfig.API_BASE_URL.toHttpUrlOrNull()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val fallbackBase = defaultBaseUrl ?: return chain.proceed(request)
        val dynamicBase = loadDynamicBaseUrl() ?: return chain.proceed(request)

        val relativePath = request.url.encodedPath
            .removePrefix(fallbackBase.encodedPath)
            .trimStart('/')

        val relativeWithQuery = buildString {
            append(relativePath)
            request.url.encodedQuery?.let {
                append('?')
                append(it)
            }
        }

        val rewrittenUrl = dynamicBase.resolve(relativeWithQuery) ?: request.url
        return chain.proceed(
            request.newBuilder()
                .url(rewrittenUrl)
                .build()
        )
    }

    private fun loadDynamicBaseUrl(): HttpUrl? {
        val savedUrl = runBlocking(Dispatchers.IO) {
            settingsDao.getById()?.sheetsUrl?.trim().orEmpty()
        }

        if (savedUrl.isBlank()) return null

        var normalized = savedUrl
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "https://$normalized"
        }

        if (normalized.endsWith("/exec", ignoreCase = true)) {
            normalized = normalized.dropLast(5)
        }

        if (!normalized.endsWith('/')) {
            normalized = "$normalized/"
        }

        return normalized.toHttpUrlOrNull()
    }
}

