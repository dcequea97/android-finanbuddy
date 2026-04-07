package com.example.finanbuddy.ui.screens.transactions

import com.example.finanbuddy.domain.data.transaction.TransactionType

sealed interface TransactionsListAction {
	data class SetFilterText(val value: String) : TransactionsListAction
	data class SetSelectedType(val value: TransactionType?) : TransactionsListAction
	data object Retry : TransactionsListAction
}