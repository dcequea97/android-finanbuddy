package com.example.finanbuddy.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    object Home : Route, NavKey

    @Serializable
    object Analytics : Route

    @Serializable
    object Settings : Route

    /**
     * Each call to AddExpense() creates a unique NavKey so Navigation 3 always
     * scopes a fresh ViewModel to the new entry, destroying it on pop.
     */
    @Serializable
    data class AddExpense(val instanceId: String = java.util.UUID.randomUUID().toString()) : Route

    /** Same unique-instance pattern as AddExpense. */
    @Serializable
    data class AddIncome(val instanceId: String = java.util.UUID.randomUUID().toString()) : Route

    /** Fresh ViewModel per visit so the transaction list reloads from Firestore. */
    @Serializable
    data class Transactions(val instanceId: String = java.util.UUID.randomUUID().toString()) : Route

    @Serializable
    object ScanReceipt : Route
}