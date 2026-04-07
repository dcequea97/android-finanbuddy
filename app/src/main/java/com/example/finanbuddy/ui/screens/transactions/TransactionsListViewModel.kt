package com.example.finanbuddy.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionsListViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(TransactionsListState())
    val state = _state.asStateFlow()
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

    fun onAction(action: TransactionsListAction) {
        when (action) {
            is TransactionsListAction.SetFilterText -> {
                _state.update { it.copy(filterText = action.value) }
                applyFilters()
            }

            is TransactionsListAction.SetSelectedType -> {
                _state.update { it.copy(selectedType = action.value) }
                applyFilters()
            }

            TransactionsListAction.Retry -> getInitData()
        }
    }

    private fun getInitData() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = transactionRepository.getTransactions()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(transactions = result.data.sortedByDescending { tx -> tx.date.atTime(tx.time) })
                    }
                    applyFilters()
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.message.ifBlank { "Could not load transactions" },
                            groupedTransactions = linkedMapOf()
                        )
                    }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun applyFilters() {
        val currentState = _state.value
        val filtered = filterTransactions(
            transactions = currentState.transactions,
            filterText = currentState.filterText,
            selectedType = currentState.selectedType
        )
        _state.update { it.copy(groupedTransactions = groupTransactionsByDate(filtered)) }
    }
}