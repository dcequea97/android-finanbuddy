package com.example.finanbuddy.ui.screens.incomes

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

class IncomesViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(IncomesState())
    val state = _state.asStateFlow()
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IncomesState())

    private fun loadInitialData() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            categoriesRepository.getIncomesCategoriesFlow()
                .collect { categories ->
                    _state.update { it.copy(categories = categories, isLoading = false) }
                }
        }
    }

    fun onAction(action: IncomesAction) {
        when (action) {
            IncomesAction.SaveTransaction -> {
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
                    type = TransactionType.INCOME,
                    amount = parsedAmount / 100,
                    date = currentState.selectedDate,
                    time = currentState.selectedTime,
                    category = selectedCategory,
                    note = ""
                )
                viewModelScope.launch {
                    transactionRepository.saveTransaction(transaction)
                        .onSuccess {
                            _state.update {
                                it.copy(saveMessage = "Ingreso guardado correctamente.", isSaveSuccess = true)
                            }
                        }
                        .onError { message ->
                            _state.update {
                                it.copy(
                                    saveMessage = message.ifBlank { "No se pudo guardar el ingreso." },
                                    isSaveSuccess = false
                                )
                            }
                        }
                        .onFinally { stopLoading() }
                }
            }
            is IncomesAction.SetAmount -> {
                _state.update { it.copy(amount = action.amount, saveMessage = null, isSaveSuccess = false) }
            }
            is IncomesAction.SetSelectedCategory -> {
                _state.update { it.copy(selectedCategory = action.categoryId, saveMessage = null, isSaveSuccess = false) }
            }
            is IncomesAction.SetSelectedDate -> {
                _state.update { it.copy(selectedDate = action.date, saveMessage = null, isSaveSuccess = false) }
            }
            is IncomesAction.SetSelectedTime -> {
                _state.update { it.copy(selectedTime = action.time, saveMessage = null, isSaveSuccess = false) }
            }
        }
    }

    private fun startLoading() = _state.update { it.copy(isLoading = true) }
    private fun stopLoading() =_state.update { it.copy(isLoading = false) }
}