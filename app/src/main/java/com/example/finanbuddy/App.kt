package com.example.finanbuddy

import android.app.Application
import com.example.finanbuddy.di.appModule
import com.example.finanbuddy.domain.repository.CategoriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(appModule)
        }
        
        // Initialize default categories if they don't exist
        initializeCategories()
    }
    
    private fun initializeCategories() {
        val categoriesRepository: CategoriesRepository by inject(CategoriesRepository::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            categoriesRepository.initializeDefaultCategories()
        }
    }
}