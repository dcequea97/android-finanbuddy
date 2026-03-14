# AGENTS.md - FinanBuddy

## Quick commands (PowerShell, project root)
```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat test
.\gradlew.bat :app:installDebug
.\gradlew.bat connectedAndroidTest
```

## Read first (high signal files)
- `app/src/main/java/com/example/finanbuddy/App.kt`: Koin bootstrap (`startKoin { modules(appModule) }`).
- `app/src/main/java/com/example/finanbuddy/di/AppModule.kt`: DI graph, repository bindings.
- `app/src/main/java/com/example/finanbuddy/ui/navigation/AppNavigation.kt`: app routes and screen wiring.
- `app/src/main/java/com/example/finanbuddy/domain/data/Resource.kt`: `Success/Error` + `onSuccess/onError/onFinally` style.
- `app/src/main/java/com/example/finanbuddy/ui/screens/expenses/ExpenseViewModel.kt` and `ui/screens/incomes/IncomesViewModel.kt`: ViewModel + `StateFlow` pattern used across features.

## Architecture snapshot
- Single `app` module, Compose-first UI under `ui/`.
- Domain contracts and models in `domain/`; repositories are consumed via interfaces (`TransactionRepository`, `CategoriesRepository`).
- DI is Koin-only; view models are provided with `viewModel { ... }` in `appModule`.
- Navigation uses Navigation 3 (`rememberNavBackStack` + `NavDisplay`) instead of Navigation Compose classic APIs.

## Data and state patterns used here
- Repository methods return `Resource<T>`, not exceptions.
- ViewModels keep private `MutableStateFlow` and expose immutable `state` with `.asStateFlow().stateIn(...)`.
- Save actions in expense/income screens build `Transaction` from form state and call `transactionRepository.saveTransaction(...)`.
- Transaction grouping for history list is UI-side in `ui/screens/transactions/TransactionsList.kt` (`Today`, `Yesterday`, `This Week`, `This Month`, `All`).

## Firebase integration points
- Firestore-backed transaction persistence: `app/src/main/java/com/example/finanbuddy/data/repository/FirebaseTransactionRepository.kt`.
- Firestore DTO and mappers: `app/src/main/java/com/example/finanbuddy/data/remote/model/FirestoreTransactionDto.kt`.
- Active DI binding: `TransactionRepository -> FirebaseTransactionRepository` in `AppModule.kt`.
- Dependencies and plugin are in `app/build.gradle.kts` and root `build.gradle.kts`.
- Runtime requires Firebase project config in `app/google-services.json`.

## Project-specific gotchas
- `com.finanbuddy.*` files exist but app entry in manifest is `com.example.finanbuddy.App`; treat `com.finanbuddy` package as legacy unless explicitly reactivated.
- `CategoriesRepository` is still dummy/in-memory (`ExpenseRepositoryDummy`), while transactions are now persisted via Firestore.
- `Transaction` uses `LocalDate`/`LocalTime`; mappers store string values in Firestore and parse back.

## Safe change strategy
- Prefer small, vertical changes: contract -> repository impl -> Koin binding -> ViewModel state handling.
- When touching persistence, validate both `:app:assembleDebug` and `test` before handing off.
- Avoid package renames and manifest changes unless required by a feature and updated end-to-end.
