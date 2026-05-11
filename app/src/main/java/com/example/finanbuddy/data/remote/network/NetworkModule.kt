package com.example.finanbuddy.data.remote.network

import com.example.finanbuddy.BuildConfig
import com.example.finanbuddy.data.local.settings.SettingsDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }
    }

    single {
        SheetsBaseUrlInterceptor(get<SettingsDao>())
    }

    single<FirebaseIdTokenProvider> {
        FirebaseIdTokenProviderImpl(get<FirebaseAuth>())
    }

    single {
        SheetsAuthInterceptor(get<FirebaseIdTokenProvider>())
    }

    single {
        SheetsTokenAuthenticator(get<FirebaseIdTokenProvider>())
    }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .addInterceptor(get<SheetsBaseUrlInterceptor>())
            .addInterceptor(get<SheetsAuthInterceptor>())
            .addInterceptor(logging)
            .authenticator(get<SheetsTokenAuthenticator>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<SheetsApi> {
        get<Retrofit>().create(SheetsApi::class.java)
    }
}

