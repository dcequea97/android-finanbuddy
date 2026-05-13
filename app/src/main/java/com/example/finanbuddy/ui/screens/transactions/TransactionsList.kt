package com.example.finanbuddy.ui.screens.transactions

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.R
import com.example.finanbuddy.domain.data.transaction.Transaction
import com.example.finanbuddy.domain.data.transaction.TransactionType
import com.example.finanbuddy.ui.components.LabelSmall
import com.example.finanbuddy.ui.components.TitleLarge
import com.example.finanbuddy.ui.components.TransactionItem
import com.example.finanbuddy.ui.navigation.AppScaffold
import com.example.finanbuddy.ui.navigation.NavigationAction
import com.example.finanbuddy.ui.navigation.Route
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
    AppScaffold(

    ) {
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
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                TitleLarge(text = stringResource(R.string.title_history))
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = stringResource(R.string.cd_filter),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                value = state.filterText,
                onValueChange = { onAction(TransactionsListAction.SetFilterText(it)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = stringResource(R.string.cd_search))
                },
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text(stringResource(R.string.placeholder_search_transactions)) },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    label = { LabelSmall(text = stringResource(R.string.filter_all)) },
                    selected = state.selectedType == null,
                    onClick = { onAction(TransactionsListAction.SetSelectedType(null)) }
                )

                TransactionType.entries.forEach { type ->
                    FilterChip(
                        label = { LabelSmall(text = type.name) },
                        selected = state.selectedType == type,
                        onClick = { onAction(TransactionsListAction.SetSelectedType(type)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.loading_transactions))
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.errorMessage)
                            TextButton(onClick = { onAction(TransactionsListAction.Retry) }) {
                                Text(stringResource(R.string.btn_retry))
                            }
                        }
                    }
                }

                state.groupedTransactions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.no_transactions_found))
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.groupedTransactions.entries.forEach { entry ->
                            item {
                                SectionHeader(title = stringResource(entry.key.labelRes()))
                            }
                            items(entry.value) { tx ->
                                TransactionItem(
                                    transaction = tx,
                                    onClick = {
                                        if (tx.type == TransactionType.EXPENSE) {
                                            onNavAction(
                                                NavigationAction.Navigate(
                                                    Route.AddExpense(
                                                        editTransactionId = tx.id,
                                                        editCategory = tx.category,
                                                        editAmount = kotlin.math.abs(tx.amount),
                                                        editDateIso = tx.date.toString(),
                                                        editNote = tx.note
                                                    )
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }
                }
            }
        }
    }
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

private fun sampleTransactions(): List<Transaction> = listOf(
    Transaction(
        id = 1,
        type = TransactionType.EXPENSE,
        amount = 5.5,
        date = LocalDate.now().minusDays(1),
        time = LocalTime.now(),
        category = "\uD83C\uDF10Internet",
        note = "Coffee"
    ),
    Transaction(
        id = 2,
        type = TransactionType.EXPENSE,
        amount = 24.5,
        date = LocalDate.now().minusDays(2),
        time = LocalTime.now().minusHours(2),
        category = "\uD83D\uDED2Mercado",
        note = "Transport"
    ),
    Transaction(
        id = 3,
        type = TransactionType.INCOME,
        amount = 350.0,
        date = LocalDate.now().minusDays(8),
        time = LocalTime.now().minusHours(4),
        category = "Freelance Payment",
        note = "Income"
    )
)

@Preview(showBackground = true)
@Composable
private fun Preview() {
    FinanBuddyTheme {
        val sample = sampleTransactions()
        TransactionsListScreen(
            state = TransactionsListState(
                transactions = sample,
                groupedTransactions = groupTransactionsByDate(sample)
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
        val sample = sampleTransactions()
        TransactionsListScreen(
            state = TransactionsListState(
                transactions = sample,
                groupedTransactions = groupTransactionsByDate(sample)
            ),
            onAction = {},
            onNavAction = {}
        )
    }
}