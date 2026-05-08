package com.example.finanbuddy.di

import com.example.finanbuddy.R
import com.example.finanbuddy.data.remote.network.SheetsApi
import com.example.finanbuddy.data.remote.network.networkModule
import com.example.finanbuddy.data.repository.firebase.FirebaseAuthRepository
import com.example.finanbuddy.data.repository.firebase.FirebaseTransactionRepository
import com.example.finanbuddy.data.repository.firebase.FirebaseCategoryRepository
import com.example.finanbuddy.data.repository.sheets.SheetsCategoriesRepository
import com.example.finanbuddy.data.repository.sheets.SheetsTransactionRepository
import com.example.finanbuddy.domain.repository.AuthRepository
import com.example.finanbuddy.domain.repository.CategoriesRepository
import com.example.finanbuddy.domain.repository.TransactionRepository
import com.example.finanbuddy.ui.screens.auth.LoginViewModel
import com.example.finanbuddy.ui.screens.expenses.ExpenseViewModel
import com.example.finanbuddy.ui.screens.home.HomeViewModel
import com.example.finanbuddy.ui.screens.incomes.IncomesViewModel
import com.example.finanbuddy.ui.screens.transactions.TransactionsListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private const val FIRESTORE_DATABASE_ID = "finan-buddy-db"

val appModule = module {
    includes(networkModule)

    single<SheetsApi> {
        get<Retrofit>().create(SheetsApi::class.java)
    }

    single<CategoriesRepository> {
//        FirebaseCategoryRepository(get())
        SheetsCategoriesRepository(get())
    }

    single<TransactionRepository> {
//        FirebaseTransactionRepository(get(), get())
        SheetsTransactionRepository(get())
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
}