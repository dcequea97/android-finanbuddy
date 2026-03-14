package com.example.finanbuddy.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.domain.data.onSuccess
import com.example.finanbuddy.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionsListViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(TransactionsListState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                getInitData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TransactionsListState()
        )

    fun onAction(@Suppress("UNUSED_PARAMETER") action: TransactionsListAction) {
        // No actions yet.
    }

    private fun getInitData() {
        viewModelScope.launch {
            transactionRepository.getTransactions()
                .onSuccess { data -> _state.update { it.copy(transactions = data) } }
        }
    }
}