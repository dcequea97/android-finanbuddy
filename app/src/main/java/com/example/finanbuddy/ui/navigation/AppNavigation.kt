package com.example.finanbuddy.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.finanbuddy.ui.screens.expenses.ExpenseRoot
import com.example.finanbuddy.ui.screens.home.HomeScreen
import com.example.finanbuddy.ui.screens.incomes.IncomesRoot

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(Route.Home)

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
            //        entryDecorators = listOf(
            //            rememberSaveableStateHolderNavEntryDecorator(),
            //        ),
            entryProvider = entryProvider {
                entry<Route.Home> {
                    HomeScreen(
                        onNavigation = { route -> backStack.navigate(route) },
                        currentRoute = backStack.currentRoute()
                    )
                }

                entry<Route.AddExpense> { _ ->
                    ExpenseRoot()
                }

                entry<Route.AddIncome> {
                    IncomesRoot()
                }

                entry<Route.ScanReceipt> {

                }
            }
        )
    }
}