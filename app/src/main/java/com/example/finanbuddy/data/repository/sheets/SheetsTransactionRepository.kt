package com.example.finanbuddy.data.repository.sheets

import com.example.finanbuddy.data.remote.model.SheetSaveData
import com.example.finanbuddy.data.remote.model.toDomain
import com.example.finanbuddy.data.remote.network.SheetsApi
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionTotals
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.domain.repository.TransactionRepository
import java.time.LocalDate

class SheetsTransactionRepository(
    val api: SheetsApi
): TransactionRepository {
    override suspend fun saveTransaction(transaction: Transaction): Resource<Long> {
        try {
            val txApi = SheetSaveData(
                categoria = transaction.category,
                monto = transaction.amount,
                concepto = transaction.note,
                fecha = transaction.date.toString()
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
        try {
            val currentMonth = LocalDate.now().monthValue
            val response = api.getSummary(month = currentMonth)
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
        try {
            val response = api.getAllTransactions(month = month)
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
}