package com.example.finanbuddy.di
import androidx.room.Room
import com.example.finanbuddy.R
import com.example.finanbuddy.data.local.settings.FinanBuddyDatabase
import com.example.finanbuddy.data.local.settings.SettingsDao
import com.example.finanbuddy.data.local.transaction.CategoryDao
import com.example.finanbuddy.data.local.transaction.TransactionDao
import com.example.finanbuddy.data.remote.network.networkModule
import com.example.finanbuddy.data.repository.cache.CachedCategoriesRepository
import com.example.finanbuddy.data.repository.cache.CachedTransactionRepository
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
private const val FIRESTORE_DATABASE_ID = "finan-buddy-db"
private const val QUALIFIER_REMOTE = "remote"
val appModule = module {
    includes(networkModule)
    // Application-level CoroutineScope for background syncs in cached repositories
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    single<FinanBuddyDatabase> {
        Room.databaseBuilder(
            androidContext(),
            FinanBuddyDatabase::class.java,
            "finanbuddy.db"
        ).fallbackToDestructiveMigration(true).build()
    }
    single<SettingsDao> { get<FinanBuddyDatabase>().settingsDao() }
    single<TransactionDao> { get<FinanBuddyDatabase>().transactionDao() }
    single<CategoryDao> { get<FinanBuddyDatabase>().categoryDao() }
    single<SettingsRepository> { RoomSettingsRepository(get()) }
    // Remote (network) repositories
    single<CategoriesRepository>(named(QUALIFIER_REMOTE)) {
        SheetsCategoriesRepository(get(), get())
    }
    single<TransactionRepository>(named(QUALIFIER_REMOTE)) {
        SheetsTransactionRepository(get(), get())
    }
    // Cached repositories wrapping the remote ones
    single<CategoriesRepository> {
        CachedCategoriesRepository(
            remoteRepository = get(named(QUALIFIER_REMOTE)),
            categoryDao = get(),
            externalScope = get()
        )
    }
    single<TransactionRepository> {
        CachedTransactionRepository(
            remoteRepository = get(named(QUALIFIER_REMOTE)),
            transactionDao = get(),
            externalScope = get()
        )
    }
    single<AuthRepository> {
        FirebaseAuthRepository(
            firebaseAuth = get(),
            context = androidContext(),
            googleWebClientId = androidContext().getString(R.string.google_web_client_id)
        )
    }
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<FirebaseFirestore> { FirebaseFirestore.getInstance(FIRESTORE_DATABASE_ID) }
    viewModel { ExpenseViewModel(get(), get()) }
    viewModel { IncomesViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { TransactionsListViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
