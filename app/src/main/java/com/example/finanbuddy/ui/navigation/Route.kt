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
    data class AddExpense(val id: Int): Route

    @Serializable
    data class AddIncome(val id: Int): Route

    @Serializable
    object ScanReceipt: Route
}