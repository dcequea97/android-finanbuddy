package com.example.finanbuddy.di

import com.example.finanbuddy.domain.repository.CategoriesRepository
import com.example.finanbuddy.domain.repository.ExpenseRepositoryDummy
import com.example.finanbuddy.domain.repository.TransactionRepository
import com.example.finanbuddy.domain.repository.TransactionRepositoryDummy
import com.example.finanbuddy.ui.screens.expenses.ExpenseViewModel
import com.example.finanbuddy.ui.screens.incomes.IncomesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.math.sin

val appModule = module {
    single<CategoriesRepository> {
        ExpenseRepositoryDummy()
    }

    single<TransactionRepository> {
        TransactionRepositoryDummy()
    }

    viewModel {
        ExpenseViewModel(get(), get())
    }

    viewModel {
        IncomesViewModel(get())
    }
}