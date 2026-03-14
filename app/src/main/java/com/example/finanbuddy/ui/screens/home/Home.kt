package com.example.finanbuddy.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.ui.components.FinanCard
import com.example.finanbuddy.ui.components.TransactionItem
import com.example.finanbuddy.ui.navigation.AppScaffold
import com.example.finanbuddy.ui.navigation.Route
import com.example.finanbuddy.ui.theme.FinanBuddyTheme
import com.example.finanbuddy.ui.theme.FinanColors
import com.example.finanbuddy.utils.ext.format
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoot(
    onNavigation: (Route) -> Unit,
    currentRoute: Route,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onNavigation = onNavigation,
        currentRoute = currentRoute
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onNavigation: (Route) -> Unit,
    currentRoute: Route,
    modifier: Modifier = Modifier
) {
    AppScaffold(
        onNavigation = onNavigation,
        currentRoute = currentRoute,
        modifier = modifier,
        showBottomBar = true,
        showActionButton = true,
        showHeader = true
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Balance
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Total Balance",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$4,250.00",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Stats cards grid
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                title = "Income",
                                amount = state.totalMonthIncomeAmount.format(2),
                                deltaText = state.differencePercentageIncome.format(2),
                                bgColor = FinanColors.Income,
                                iconTint = FinanColors.OnIncome,
                                icon = Icons.Rounded.ArrowDownward,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigation(Route.AddIncome) }
                            )
                            StatCard(
                                title = "Expenses",
                                amount = state.totalMonthExpenseAmount.format(2),
                                deltaText = state.differencePercentageExpense.format(2),
                                bgColor = FinanColors.Expense,
                                iconTint = FinanColors.OnExpense,
                                icon = Icons.Rounded.ArrowUpward,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigation(Route.AddExpense) }
                            )
                        }

                        // Activity card
                        ActivityCard()

                        // Transactions
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Transactions",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "View All",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onNavigation(Route.Transactions) })
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                state.transactions.forEach { tx ->
                                    TransactionItem(transaction = tx)
                                }

                                if (state.transactions.isEmpty() && !state.isLoading) {
                                    Text(
                                        text = "No recent transactions",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun StatCard(
    title: String,
    amount: String,
    deltaText: String,
    bgColor: Color,
    iconTint: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FinanCard(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgColor), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint
                    )
                }


                Text(
                    text = "$deltaText%",
                    color = iconTint,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(bgColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = amount,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActivityCard() {
    FinanCard(
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Activity",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Last 30 Days",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = {}) {
                    Text(
                        text = "⋯",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {

                // Capture theme color outside Canvas
                val primary = MaterialTheme.colorScheme.primary

                // Simple chart using Canvas + gradient fill
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, h * 0.8f)
                        cubicTo(w * 0.1f, h * 0.8f, w * 0.1f, h * 0.4f, w * 0.2f, h * 0.4f)
                        cubicTo(w * 0.3f, h * 0.4f, w * 0.3f, h * 0.7f, w * 0.4f, h * 0.7f)
                        cubicTo(w * 0.5f, h * 0.7f, w * 0.5f, h * 0.2f, w * 0.6f, h * 0.2f)
                        cubicTo(w * 0.7f, h * 0.2f, w * 0.7f, h * 0.5f, w * 0.8f, h * 0.5f)
                        cubicTo(w * 0.9f, h * 0.5f, w * 0.9f, h * 0.1f, w, h * 0.1f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            listOf(
                                primary.copy(alpha = 0.15f),
                                primary.copy(alpha = 0f)
                            )
                        )
                    )

                    // stroke
                    val strokePath = Path().apply {
                        moveTo(0f, h * 0.8f)
                        cubicTo(w * 0.1f, h * 0.8f, w * 0.1f, h * 0.4f, w * 0.2f, h * 0.4f)
                        cubicTo(w * 0.3f, h * 0.4f, w * 0.3f, h * 0.7f, w * 0.4f, h * 0.7f)
                        cubicTo(w * 0.5f, h * 0.7f, w * 0.5f, h * 0.2f, w * 0.6f, h * 0.2f)
                        cubicTo(w * 0.7f, h * 0.2f, w * 0.7f, h * 0.5f, w * 0.8f, h * 0.5f)
                        cubicTo(w * 0.9f, h * 0.5f, w * 0.9f, h * 0.1f, w, h * 0.1f)
                    }
                    drawPath(
                        path = strokePath,
                        color = primary,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 3f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // bottom labels
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val labels = listOf("1 Nov", "7 Nov", "14 Nov", "21 Nov", "Today")
                    labels.forEachIndexed { idx, lbl ->
                        Text(
                            text = lbl,
                            color = if (idx == labels.lastIndex) primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FinanBuddyTheme {
        HomeScreen(
            state = HomeState(),
            onNavigation = {},
            currentRoute = Route.Home
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenDarkPreview() {
    FinanBuddyTheme {
        HomeScreen(
            state = HomeState(),
            onNavigation = {},
            currentRoute = Route.Home
        )
    }
}