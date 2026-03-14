package com.example.finanbuddy.ui.screens.transactions

import com.example.finanbuddy.domain.data.transaction.Transaction

data class TransactionsListState(
    val transactions: List<Transaction> = emptyList(),
)