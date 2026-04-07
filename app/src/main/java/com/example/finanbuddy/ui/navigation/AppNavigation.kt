package com.example.finanbuddy.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.finanbuddy.domain.repository.AuthRepository
import com.example.finanbuddy.ui.screens.auth.LoginRoot
import com.example.finanbuddy.ui.screens.auth.RegisterRoot
import com.example.finanbuddy.ui.screens.expenses.ExpenseRoot
import com.example.finanbuddy.ui.screens.home.HomeRoot
import com.example.finanbuddy.ui.screens.incomes.IncomesRoot
import com.example.finanbuddy.ui.screens.transactions.TransactionsListRoot
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val authRepository: AuthRepository = koinInject()
    val startRoute = if (authRepository.isUserLoggedIn()) Route.Home else Route.Login
    val backStack = rememberNavBackStack(startRoute)
    val onLogout = {
        authRepository.signOut()
        backStack.resetTo(Route.Login)
    }
    val onNavigationAction = { action: NavigationAction ->
        when (action) {
            is NavigationAction.Navigate -> backStack.navigate(action.route)
            NavigationAction.Pop -> backStack.pop()
            is NavigationAction.PopTo -> backStack.popTo(action.route)
            NavigationAction.PopToRoot -> backStack.popToRoot()
            is NavigationAction.NavigateAndPopTo -> backStack.navigateAndPopTo(
                action.route,
                action.popTo
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.pop() },
            transitionSpec = NavigationAnimations.transitionSpec,
            popTransitionSpec = NavigationAnimations.popTransitionSpec,
            predictivePopTransitionSpec = NavigationAnimations.predictivePopTransitionSpec,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<Route.Login> {
                    LoginRoot(onNavAction = onNavigationAction)
                }

                entry<Route.Register> {
                    RegisterRoot(onNavAction = onNavigationAction)
                }

                entry<Route.Home> {
                    HomeRoot(
                        onNavigation = { route -> onNavigationAction(NavigationAction.Navigate(route)) },
                        currentRoute = backStack.currentRoute(),
                        onLogout = onLogout
                    )
                }

                entry<Route.AddExpense> {
                    ExpenseRoot(onNavAction = onNavigationAction)
                }

                entry<Route.AddIncome> {
                    IncomesRoot(onNavAction = onNavigationAction)
                }

                entry<Route.ScanReceipt> {

                }

                entry<Route.Transactions> {
                    TransactionsListRoot(onNavAction = onNavigationAction)
                }
            }
        )
    }
}