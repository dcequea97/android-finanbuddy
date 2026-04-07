package com.example.finanbuddy.ui.screens.expenses

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.ui.components.ConfirmButton
import com.example.finanbuddy.ui.components.DefaultHeader
import com.example.finanbuddy.ui.components.LabelSmall
import com.example.finanbuddy.ui.components.SpacerLarge
import com.example.finanbuddy.ui.components.SpacerSmall
import com.example.finanbuddy.ui.components.SpacerXLarge
import com.example.finanbuddy.ui.components.inputs.AmountTextField
import com.example.finanbuddy.ui.components.inputs.CategorySection
import com.example.finanbuddy.ui.components.inputs.DateTimeSection
import com.example.finanbuddy.ui.components.inputs.NoteSection
import com.example.finanbuddy.ui.navigation.AppScaffold
import com.example.finanbuddy.ui.navigation.NavigationAction
import com.example.finanbuddy.ui.theme.FinanBuddyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExpenseRoot(
    onNavAction: (NavigationAction) -> Unit,
) {
    val viewModel: ExpenseViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onExpenseSaved.collect { onNavAction(NavigationAction.Pop) }
    }

    ExpenseScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavAction = onNavAction
    )
}

@Composable
fun ExpenseScreen(
    state: ExpenseState,
    onAction: (ExpenseAction) -> Unit,
    onNavAction: (NavigationAction) -> Unit,
    modifier: Modifier = Modifier
) {

    AppScaffold {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            DefaultHeader(onClose = { onNavAction(NavigationAction.Pop) }, title = "New Expense")

            // Main Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val horizontalPadding = Modifier.padding(horizontal = 24.dp)

                // Amount Display
                Amount(
                    amount = state.amount,
                    onValueChange = { onAction(ExpenseAction.SetAmount(it)) }
                )

                SpacerXLarge()

                // Category Selection
                CategorySection(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { categoryId ->
                        onAction(ExpenseAction.SetSelectedCategory(categoryId))
                    },
                    onSeeAll = {},
                    isLoading = state.isLoading
                )

                SpacerXLarge()

                // Date & Time
                DateTimeSection(
                    modifier = horizontalPadding,
                    selectedDate = state.selectedDate,
                    selectedTime = state.selectedTime,
                    onDateChanged = { onAction(ExpenseAction.SetSelectedDate(it)) },
                    onTimeChanged = { onAction(ExpenseAction.SetSelectedTime(it)) }
                )

                SpacerXLarge()

                // Note
                NoteSection(
                    modifier = horizontalPadding,
                    note = state.note,
                    onNoteChange = { newNote ->
                        onAction(ExpenseAction.SetNote(newNote))
                    }
                )

                SpacerLarge()
                // Confirm Button
                ConfirmButton(
                    text = "Confirm Expense",
                    modifier = horizontalPadding
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    onClick = { onAction(ExpenseAction.SaveTransaction) },
                    enabled = state.amount.isNotBlank() &&
                            state.selectedCategory?.isNotBlank() == true &&
                            !state.isLoading
                )

                state.saveMessage?.let { message ->
                    LabelSmall(
                        text = message,
                        color = if (state.isSaveSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = horizontalPadding
                    )
                }
            }
        }
    }
}


@Composable
private fun Amount(
    amount: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LabelSmall(text = "TOTAL AMOUNT")

        SpacerSmall()

        AmountTextField(
            value = amount,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.headlineLarge.copy(
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpenseScreenPreview() {
    val categories: List<CategoryModel> = emptyList()
//    runBlocking {
//        ExpenseRepositoryDummy().getCategories()
//            .onSuccess { list ->
//                categories = list
//            }
//    }
    val state = ExpenseState(
        categories = categories,
        amount = "23423421",
        selectedCategory = "food"
    )

    FinanBuddyTheme {
        ExpenseScreen(
            state = state,
            onAction = {},
            onNavAction = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExpenseScreenDarkPreview() {
    val categories: List<CategoryModel> = emptyList()
//    runBlocking {
//        ExpenseRepositoryDummy().getCategories()
//            .onSuccess { list ->
//                categories = list
//            }
//    }
    val state = ExpenseState(
        categories = categories,
        amount = "23423421",
        selectedCategory = "food"
    )

    FinanBuddyTheme {
        ExpenseScreen(
            state = state,
            onAction = {},
            onNavAction = {}
        )
    }
}