package com.example.finanbuddy.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.ui.components.LabelSmall
import com.example.finanbuddy.ui.components.TitleLarge
import com.example.finanbuddy.ui.components.TransactionItem
import com.example.finanbuddy.ui.navigation.AppScaffold
import com.example.finanbuddy.ui.navigation.NavigationAction
import com.example.finanbuddy.ui.theme.FinanBuddyTheme
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TransactionsListRoot(
    onNavAction: (NavigationAction) -> Unit,
    viewModel: TransactionsListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TransactionsListScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavAction = onNavAction
    )
}

@Composable
fun TransactionsListScreen(
    state: TransactionsListState,
    onAction: (TransactionsListAction) -> Unit,
    onNavAction: (NavigationAction) -> Unit
) {
    AppScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavAction(NavigationAction.Pop) }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "Filter",
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                TitleLarge(text = "History")
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Filter",
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            val filterText = retain { mutableStateOf("") }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                value = filterText.value,
                onValueChange = { filterText.value = it },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                },
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Search transactions...") },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            val transactionTypeSelected = retain { mutableStateOf<TransactionType?>(null) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                FilterChip(
                    label = {
                        LabelSmall(
                            text = "All",
                        )
                    },
                    selected = transactionTypeSelected.value == null,
                    onClick = { transactionTypeSelected.value = null }

                )
                TransactionType.entries.forEach {
                    FilterChip(
                        label = {
                            LabelSmall(
                                text = it.name,
                            )
                        },
                        selected = transactionTypeSelected.value == it,
                        onClick = { transactionTypeSelected.value = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transactions list
            val filteredTransactions = state.transactions
                .filter {
                    it.note.contains(filterText.value, ignoreCase = true) ||
                            it.category.contains(filterText.value, ignoreCase = true) ||
                            it.amount.toString().contains(filterText.value, ignoreCase = true)
                }
                .filter {
                    transactionTypeSelected.value?.let { selectedType ->
                        it.type == selectedType
                    } ?: true
                }

            val grouped = groupTransactionsByDate(filteredTransactions)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                grouped.entries.forEach { entry ->
                    item {
                        SectionHeader(title = entry.key)
                    }
                    items(entry.value) { tx ->
                        TransactionItem(transaction = tx)
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

// Group transactions by date into Today, Yesterday, This Week, This Month, All
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

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

//@Composable
//private fun FilterChip(text: String, selected: Boolean = false) {
//    val bg =
//        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
//    Box(
//        modifier = Modifier
//            .clip(MaterialTheme.shapes.medium)
//            .background(bg)
//            .padding(horizontal = 12.dp, vertical = 6.dp)
//    ) {
//        LabelSmall(
//            text = text,
//            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
//        )
//    }
//}

private fun sampleTransactions(): List<Transaction> = listOf(
    Transaction(
        1,
        TransactionType.EXPENSE,
        5.5,
        LocalDate.now().minusDays(1),
        LocalTime.now(),
        "Starbucks",
        "Coffee"
    ),
    Transaction(
        2,
        TransactionType.EXPENSE,
        24.5,
        LocalDate.now().minusDays(2),
        LocalTime.now().minusHours(2),
        "Uber Ride",
        "Transport"
    ),
    Transaction(
        3,
        TransactionType.INCOME,
        350.0,
        LocalDate.now().minusDays(8),
        LocalTime.now().minusHours(4),
        "Freelance Payment",
        "Income"
    )
)

@Preview(showBackground = true)
@Composable
private fun Preview() {
    FinanBuddyTheme {
        TransactionsListScreen(
            state = TransactionsListState(
                transactions = sampleTransactions()
            ),
            onAction = {},
            onNavAction = {}
        )
    }
}

@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DarkPreview() {
    FinanBuddyTheme {
        TransactionsListScreen(
            state = TransactionsListState(
                transactions = sampleTransactions()
            ),
            onAction = {},
            onNavAction = {}
        )
    }
}