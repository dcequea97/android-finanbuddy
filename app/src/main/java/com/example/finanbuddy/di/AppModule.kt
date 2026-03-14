package com.example.finanbuddy.di

import com.example.finanbuddy.R
import com.example.finanbuddy.data.repository.FirebaseAuthRepository
import com.example.finanbuddy.data.repository.FirebaseTransactionRepository
import com.example.finanbuddy.domain.repository.AuthRepository
import com.example.finanbuddy.domain.repository.CategoriesRepository
import com.example.finanbuddy.domain.repository.ExpenseRepositoryDummy
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

private const val FIRESTORE_DATABASE_ID = "finan-buddy-db"

val appModule = module {
    single<CategoriesRepository> {
        ExpenseRepositoryDummy()
    }

    single<TransactionRepository> {
        FirebaseTransactionRepository(get())
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