package com.example.finanbuddy.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {
    @Serializable
    data object Home: Route, NavKey

    @Serializable
    data object Analytics: Route, NavKey

    @Serializable
    data object Settings: Route, NavKey

    @Serializable
    data class AddExpense(val id: Int): Route, NavKey

    @Serializable
    data class AddIncome(val id: Int): Route, NavKey

    @Serializable
    data object ScanReceipt: Route, NavKey
}