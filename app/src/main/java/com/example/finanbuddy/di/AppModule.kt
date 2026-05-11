package com.example.finanbuddy.di

import androidx.room.Room
import com.example.finanbuddy.R
import com.example.finanbuddy.data.local.settings.FinanBuddyDatabase
import com.example.finanbuddy.data.local.settings.SettingsDao
import com.example.finanbuddy.data.remote.network.networkModule
import com.example.finanbuddy.data.repository.firebase.FirebaseAuthRepository
import com.example.finanbuddy.data.repository.local.RoomSettingsRepository
import com.example.finanbuddy.data.repository.sheets.SheetsCategoriesRepository
import com.example.finanbuddy.data.repository.sheets.SheetsTransactionRepository
import com.example.finanbuddy.domain.repository.AuthRepository
import com.example.finanbuddy.domain.repository.CategoriesRepository
import com.example.finanbuddy.domain.repository.SettingsRepository
import com.example.finanbuddy.domain.repository.TransactionRepository
import com.example.finanbuddy.ui.screens.auth.LoginViewModel
import com.example.finanbuddy.ui.screens.expenses.ExpenseViewModel
import com.example.finanbuddy.ui.screens.home.HomeViewModel
import com.example.finanbuddy.ui.screens.incomes.IncomesViewModel
import com.example.finanbuddy.ui.screens.settings.SettingsViewModel
import com.example.finanbuddy.ui.screens.transactions.TransactionsListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private const val FIRESTORE_DATABASE_ID = "finan-buddy-db"

val appModule = module {
    includes(networkModule)

    single<FinanBuddyDatabase> {
        Room.databaseBuilder(
            androidContext(),
            FinanBuddyDatabase::class.java,
            "finanbuddy.db"
        ).fallbackToDestructiveMigration(true).build()
    }

    single<SettingsDao> {
        get<FinanBuddyDatabase>().settingsDao()
    }

    single<SettingsRepository> {
        RoomSettingsRepository(get())
    }

    single<CategoriesRepository> {
//        FirebaseCategoryRepository(get())
        SheetsCategoriesRepository(get(), get())
    }

    single<TransactionRepository> {
//        FirebaseTransactionRepository(get(), get())
        SheetsTransactionRepository(get(), get())
    }

    single<AuthRepository> {
        FirebaseAuthRepository(
            firebaseAuth = get(),
            context = androidContext(),
            googleWebClientId = androidContext().getString(R.string.google_web_client_id)
        )
    }

    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }

    single<FirebaseFirestore> {
        FirebaseFirestore.getInstance(FIRESTORE_DATABASE_ID)
    }

    viewModel {
        ExpenseViewModel(get(), get())
    }

    viewModel {
        IncomesViewModel(get(), get())
    }

    viewModel {
        HomeViewModel(get())
    }

    viewModel {
        LoginViewModel(get())
    }

    viewModel {
        TransactionsListViewModel(get())
    }

    viewModel {
        SettingsViewModel(get())
    }
}