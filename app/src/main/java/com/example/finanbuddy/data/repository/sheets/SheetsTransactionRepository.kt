package com.example.finanbuddy.data.repository.sheets

import com.example.finanbuddy.data.remote.model.SheetSaveData
import com.example.finanbuddy.data.remote.model.toDomain
import com.example.finanbuddy.data.remote.network.FirebaseIdTokenProvider
import com.example.finanbuddy.data.remote.network.SheetsApi
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionTotals
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.TransactionRepository
import java.time.LocalDate

class SheetsTransactionRepository(
    private val api: SheetsApi,
    private val idTokenProvider: FirebaseIdTokenProvider
): TransactionRepository {
    override suspend fun saveTransaction(transaction: Transaction): Resource<Long> {
        val idToken = loadIdTokenOrNull()
            ?: return Resource.Error("User is not authenticated")

        try {
            val txApi = SheetSaveData(
                categoria = transaction.category,
                monto = transaction.amount,
                concepto = transaction.note,
                fecha = transaction.date.toString(),
                idToken = idToken
            )

            val response = api.postDataToSheets(txApi)
            if (response.isSuccessful) {
                return Resource.Success(1)
            }

            return Resource.Error(
                response.errorBody()?.string() ?: "Unknown error"
            )
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getTransactions(): Resource<List<Transaction>> {
        val currentMonth = LocalDate.now().monthValue
        return getTransactionsByMonth(month = currentMonth)
    }

    override suspend fun getRecentTransactions(limit: Int): Resource<List<Transaction>> {
        return getTransactions()
    }

    override suspend fun getTransactionsByType(type: TransactionType): Resource<List<Transaction>> {
        return getTransactions()
    }

    override suspend fun getTotalsSummary(): Resource<TransactionTotals> {
        val idToken = loadIdTokenOrNull()
            ?: return Resource.Error("User is not authenticated")

        try {
            val currentMonth = LocalDate.now().monthValue
            val response = api.getSummary(idToken = idToken, month = currentMonth)
            if (response.isSuccessful) {
                val data = response.body() ?: return Resource.Error("Unknown error")
                val totals = TransactionTotals(
                    availableAmount = data.disponibleGastar,
                    monthIncomes = data.ingresosTotales,
                    monthExpenses = data.gastadoHastaAhora,
                    previousMonthIncomes = data.gastadoHastaAhora,
                    previousMonthExpenses = data.gastadoHastaAhora,
                )
                return Resource.Success(totals)
            }

            return Resource.Error("Unknown error")
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getTransactionsForMonthYear(
        month: Int,
        year: Int
    ): Resource<List<Transaction>> {
        return getTransactionsByMonth(month)
    }

    override suspend fun getCurrentMonthTotal(type: TransactionType): Resource<Double> {
        TODO("Not yet implemented")
    }

    private suspend fun getTransactionsByMonth(month: Int): Resource<List<Transaction>> {
        val idToken = loadIdTokenOrNull()
            ?: return Resource.Error("User is not authenticated")

        try {
            val response = api.getAllTransactions(idToken = idToken, month = month)
            if (response.isSuccessful) {
                val data = response.body() ?: emptyList()
                val transactions = data.map { it.toDomain() }
                return Resource.Success(transactions)
            }

            return Resource.Error(
                response.errorBody()?.string() ?: "Unknown error"
            )
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun loadIdTokenOrNull(): String? {
        return idTokenProvider.getIdToken(forceRefresh = false)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }
}