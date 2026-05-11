package com.example.finanbuddy.ui.screens.transactions

import androidx.annotation.StringRes
import com.example.finanbuddy.R

enum class TransactionDateGroup {
    TODAY, YESTERDAY, THIS_WEEK, THIS_MONTH, ALL;

    @StringRes
    fun labelRes(): Int = when (this) {
        TODAY -> R.string.group_today
        YESTERDAY -> R.string.group_yesterday
        THIS_WEEK -> R.string.group_this_week
        THIS_MONTH -> R.string.group_this_month
        ALL -> R.string.group_all
    }
}

