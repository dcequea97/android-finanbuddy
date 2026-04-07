package com.example.finanbuddy.ui.screens.transactions

import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType

data class TransactionsListState(
    val transactions: List<Transaction> = emptyList(),
    val groupedTransactions: LinkedHashMap<String, List<Transaction>> = linkedMapOf(),
    val filterText: String = "",
    val selectedType: TransactionType? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)