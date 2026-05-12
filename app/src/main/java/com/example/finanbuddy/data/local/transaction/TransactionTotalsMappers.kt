package com.example.finanbuddy.data.local.transaction

import com.example.finanbuddy.domain.data.transaction.TransactionTotals

fun TransactionTotalsEntity.toDomain(): TransactionTotals {
    return TransactionTotals(
        availableAmount = availableAmount,
        monthIncomes = monthIncomes,
        monthExpenses = monthExpenses,
        previousMonthIncomes = previousMonthIncomes,
        previousMonthExpenses = previousMonthExpenses
    )
}

fun TransactionTotals.toEntity(): TransactionTotalsEntity {
    return TransactionTotalsEntity(
        availableAmount = availableAmount,
        monthIncomes = monthIncomes,
        monthExpenses = monthExpenses,
        previousMonthIncomes = previousMonthIncomes,
        previousMonthExpenses = previousMonthExpenses
    )
}
