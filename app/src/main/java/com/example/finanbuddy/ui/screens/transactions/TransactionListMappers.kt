package com.example.finanbuddy.ui.screens.transactions

import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import java.time.LocalDate

fun filterTransactions(
    transactions: List<Transaction>,
    filterText: String,
    selectedType: TransactionType?
): List<Transaction> {
    return transactions
        .asSequence()
        .filter { tx ->
            val query = filterText.trim()
            query.isBlank() ||
                tx.note.contains(query, ignoreCase = true) ||
                tx.category.contains(query, ignoreCase = true) ||
                tx.amount.toString().contains(query, ignoreCase = true)
        }
        .filter { tx -> selectedType?.let { tx.type == it } ?: true }
        .toList()
}

fun groupTransactionsByDate(transactions: List<Transaction>): LinkedHashMap<String, List<Transaction>> {
    val now = LocalDate.now()
    val yesterday = now.minusDays(1)
    val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
    val startOfMonth = now.withDayOfMonth(1)

    val todayList = mutableListOf<Transaction>()
    val yesterdayList = mutableListOf<Transaction>()
    val weekList = mutableListOf<Transaction>()
    val monthList = mutableListOf<Transaction>()
    val allList = mutableListOf<Transaction>()

    transactions.forEach { tx ->
        when {
            tx.date == now -> todayList.add(tx)
            tx.date == yesterday -> yesterdayList.add(tx)
            tx.date >= startOfWeek -> weekList.add(tx)
            tx.date >= startOfMonth -> monthList.add(tx)
            else -> allList.add(tx)
        }
    }

    val result = LinkedHashMap<String, List<Transaction>>()
    if (todayList.isNotEmpty()) result["Today"] = todayList
    if (yesterdayList.isNotEmpty()) result["Yesterday"] = yesterdayList
    if (weekList.isNotEmpty()) result["This Week"] = weekList
    if (monthList.isNotEmpty()) result["This Month"] = monthList
    if (allList.isNotEmpty()) result["All"] = allList

    return result
}

