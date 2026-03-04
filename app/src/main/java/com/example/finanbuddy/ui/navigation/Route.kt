package com.example.finanbuddy.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {
    @Serializable
    object Home: Route, NavKey

    @Serializable
    object Analytics: Route

    @Serializable
    object Settings: Route

    @Serializable
    data object AddExpense: Route

    @Serializable
    data object AddIncome: Route

    @Serializable
    data object Transactions: Route

    @Serializable
    object ScanReceipt: Route
}