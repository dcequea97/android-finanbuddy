package com.example.finanbuddy.ui.screens.incomes

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.domain.data.onSuccess
import com.example.finanbuddy.domain.repository.ExpenseRepositoryDummy
import com.example.finanbuddy.ui.components.CategoryCardType
import com.example.finanbuddy.ui.components.ConfirmButton
import com.example.finanbuddy.ui.components.DefaultHeader
import com.example.finanbuddy.ui.components.LabelSmall
import com.example.finanbuddy.ui.components.SpacerXLarge
import com.example.finanbuddy.ui.components.SpacerXSmall
import com.example.finanbuddy.ui.components.inputs.AmountTextField
import com.example.finanbuddy.ui.components.inputs.CategorySection
import com.example.finanbuddy.ui.components.inputs.DateTimeSection
import com.example.finanbuddy.ui.navigation.AppScaffold
import com.example.finanbuddy.ui.navigation.NavigationAction
import com.example.finanbuddy.ui.theme.FinanBuddyTheme
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.koinViewModel

@Composable
fun IncomesRoot(
    onNavAction: (NavigationAction) -> Unit,
    viewModel: IncomesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    IncomesScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavAction = onNavAction
    )
}

@Composable
fun IncomesScreen(
    state: IncomesState,
    onAction: (IncomesAction) -> Unit,
    onNavAction: (NavigationAction) -> Unit,
) {
    AppScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            DefaultHeader(onClose = { onNavAction(NavigationAction.Pop) }, title = "New Income")

            // Main Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                val horizontalPadding = Modifier.padding(horizontal = 24.dp)

                // Amount Display
                Amount(
                    amount = state.amount,
                    onValueChange = { onAction(IncomesAction.SetAmount(it)) }
                )

                SpacerXLarge()

                // Category Selection
                CategorySection(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { categoryId ->
                        onAction(IncomesAction.SetSelectedCategory(categoryId))
                    },
                    onSeeAll = {},
                    cardType = CategoryCardType.SIMPLE
                )

                SpacerXLarge()

                // Date & Time
                DateTimeSection(
                    modifier = horizontalPadding,
                    selectedDate = state.selectedDate,
                    selectedTime = state.selectedTime,
                    onDateChanged = { onAction(IncomesAction.SetSelectedDate(it)) },
                    onTimeChanged = { onAction(IncomesAction.SetSelectedTime(it)) }
                )

                SpacerXLarge()

                // Confirm Button
                ConfirmButton(
                    text = "Register income",
                    modifier = horizontalPadding
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    onClick = { onAction(IncomesAction.SaveTransaction) },
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
        AmountTextField(
            value = amount,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.headlineLarge.copy(
                textAlign = TextAlign.Center
            )
        )
        SpacerXSmall()
        LabelSmall(text = "Enter amount")
    }
}

@Preview
@Composable
private fun Preview() {
    var categories: List<CategoryModel> = emptyList()
    runBlocking {
        ExpenseRepositoryDummy().getCategories()
            .onSuccess { list ->
                categories = list
            }
    }

    FinanBuddyTheme {
        IncomesScreen(
            state = IncomesState(
                categories = categories,
                selectedCategory = "food",
                amount = ""
            ),
            onAction = {},
            onNavAction = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDark() {
    var categories: List<CategoryModel> = emptyList()
    runBlocking {
        ExpenseRepositoryDummy().getCategories()
            .onSuccess { list ->
                categories = list
            }
    }

    FinanBuddyTheme {
        IncomesScreen(
            state = IncomesState(
                categories = categories,
                selectedCategory = "food",
                amount = ""
            ),
            onAction = {},
            onNavAction = {}
        )
    }
}
