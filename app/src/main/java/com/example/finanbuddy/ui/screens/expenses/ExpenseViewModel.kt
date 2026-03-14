package com.example.finanbuddy.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.domain.data.onError
import com.example.finanbuddy.domain.data.onFinally
import com.example.finanbuddy.domain.data.onSuccess
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.CategoriesRepository
import com.example.finanbuddy.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ExpenseState())
    val state = _state.asStateFlow()
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseState())


    private fun loadInitialData() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            categoriesRepository.getCategories()
                .onSuccess { categories ->
                    _state.update { it.copy(categories = categories) }
                }
                .onError { _ ->
                    // TODO()
                }
                .onFinally { _state.update { it.copy(isLoading = false) } }
        }
    }

    fun onAction(action: ExpenseAction) {
        when (action) {
            is ExpenseAction.SetSelectedCategory -> {
                _state.update { it.copy(selectedCategory = action.categoryId, saveMessage = null, isSaveSuccess = false) }
            }

            is ExpenseAction.SetAmount -> {
                _state.update { it.copy(amount = action.amount, saveMessage = null, isSaveSuccess = false) }
            }

            is ExpenseAction.SetSelectedDate -> {
                _state.update { it.copy(selectedDate = action.date, saveMessage = null, isSaveSuccess = false) }
            }

            is ExpenseAction.SetSelectedTime -> {
                _state.update { it.copy(selectedTime = action.time, saveMessage = null, isSaveSuccess = false) }
            }

            is ExpenseAction.SetNote -> {
                _state.update { it.copy(note = action.note, saveMessage = null, isSaveSuccess = false) }
            }

            ExpenseAction.SaveTransaction -> {
                startLoading()
                val currentState = _state.value
                val selectedCategory = currentState.selectedCategory?.takeIf { it.isNotBlank() }
                val parsedAmount = currentState.amount.toDoubleOrNull()

                if (selectedCategory == null || parsedAmount == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            saveMessage = "Completa categoria y monto valido.",
                            isSaveSuccess = false
                        )
                    }
                    return
                }

                val transaction = Transaction(
                    id = 0,
                    type = TransactionType.EXPENSE,
                    amount = parsedAmount / 100,
                    date = currentState.selectedDate,
                    time = currentState.selectedTime,
                    category = selectedCategory,
                    note = currentState.note
                )
                viewModelScope.launch {
                    transactionRepository.saveTransaction(transaction)
                        .onSuccess {
                            _state.update {
                                it.copy(saveMessage = "Gasto guardado correctamente.", isSaveSuccess = true)
                            }
                        }
                        .onError { message ->
                            _state.update {
                                it.copy(
                                    saveMessage = message.ifBlank { "No se pudo guardar el gasto." },
                                    isSaveSuccess = false
                                )
                            }
                        }
                        .onFinally { stopLoading() }
                }
            }
        }
    }

    private fun startLoading() = _state.update { it.copy(isLoading = true) }
    private fun stopLoading() =_state.update { it.copy(isLoading = false) }
}