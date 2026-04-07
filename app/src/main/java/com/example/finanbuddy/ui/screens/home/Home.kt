package com.example.finanbuddy.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.ui.components.AppLoader
import com.example.finanbuddy.ui.components.FinanCard
import com.example.finanbuddy.ui.components.PullToRefreshBox
import com.example.finanbuddy.ui.components.TransactionItem
import com.example.finanbuddy.ui.navigation.AppScaffold
import com.example.finanbuddy.ui.navigation.Route
import com.example.finanbuddy.ui.theme.FinanBuddyTheme
import com.example.finanbuddy.ui.theme.FinanColors
import com.example.finanbuddy.utils.ext.format
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import org.koin.androidx.compose.koinViewModel
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

@Composable
fun HomeRoot(
    onNavigation: (Route) -> Unit,
    currentRoute: Route,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onNavigation = onNavigation,
        onAction = viewModel::onAction,
        currentRoute = currentRoute,
        onLogout = onLogout
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun HomeScreen(
    state: HomeState,
    onNavigation: (Route) -> Unit,
    onAction: (HomeAction) -> Unit,
    currentRoute: Route,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppScaffold(
        onNavigation = onNavigation,
        onLogout = onLogout,
        currentRoute = currentRoute,
        modifier = modifier,
        showBottomBar = true,
        showActionButton = true,
        showHeader = true
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(HomeAction.OnRefresh) },
                modifier = Modifier.fillMaxSize()
            ) {
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
                                text = "$${state.availableBalance}",
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
                                onClick = { onNavigation(Route.AddIncome()) },
                                showLoading = state.isLoadingTransactions
                            )
                            StatCard(
                                title = "Expenses",
                                amount = state.totalMonthExpenseAmount.format(2),
                                deltaText = state.differencePercentageExpense.format(2),
                                bgColor = FinanColors.Expense,
                                iconTint = FinanColors.OnExpense,
                                icon = Icons.Rounded.ArrowUpward,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigation(Route.AddExpense()) },
                                showLoading = state.isLoadingTransactions
                            )
                        }

                        // Activity card
                        ActivityCard(
                            incomes = state.transactionsIncomesByMonth,
                            expenses = state.transactionsExpensesByMonth
                        )

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
                                    modifier = Modifier.clickable { onNavigation(Route.Transactions()) })
                            }

                            AnimatedContent(
                                targetState = state.isLoadingTransactions,
                                transitionSpec = {
                                    (slideInVertically(
                                        initialOffsetY = { fullHeight -> -fullHeight / 3 },
                                        animationSpec = tween(260)
                                    ) + fadeIn(animationSpec = tween(260))) togetherWith
                                            (slideOutVertically(
                                                targetOffsetY = { fullHeight -> fullHeight / 3 },
                                                animationSpec = tween(220)
                                            ) + fadeOut(animationSpec = tween(220)))
                                },
                                label = "recent-transactions-loading-transition"
                            ) { isLoading ->
                                if (isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppLoader()
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        state.transactions.forEach { tx ->
                                            TransactionItem(transaction = tx)
                                        }

                                        if (state.transactions.isEmpty()) {
                                            Text(
                                                text = "No recent transactions",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StatCard(
    title: String,
    amount: String,
    deltaText: String,
    bgColor: Color,
    iconTint: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    showLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() }
    ) {
        FinanCard {
            AnimatedContent(
                targetState = showLoading,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                        animationSpec = tween(
                            220
                        )
                    )
                },
                label = "stat-card-loading-transition"
            ) { isLoading ->
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppLoader()
                    }

                    return@AnimatedContent
                }
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
    }
}

@Composable
private fun ActivityCard(
    incomes: List<Double>?,
    expenses: List<Double>?,
) {
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
                    .height(180.dp)
            ) {
                // Capture theme color outside Canvas
                if (incomes == null || expenses == null) {
                    AppLoader(
                        modifier = Modifier.align(Alignment.Center)
                    )
                    return@Column
                }

                // Ensure minimum data points for chart rendering
                val normalizedIncomes = if (incomes.size == 1) {
                    listOf(incomes[0], incomes[0])
                } else {
                    incomes
                }

                val normalizedExpenses = if (expenses.size == 1) {
                    listOf(expenses[0], expenses[0])
                } else {
                    expenses
                }

                var maxValue =
                    (normalizedIncomes.maxOrNull()?.coerceAtLeast(normalizedExpenses.maxOrNull() ?: 0.0)) ?: 1000.0
                maxValue = if (maxValue == 0.0) 1000.0 else maxValue
                
                val magnitude = if (maxValue > 1.0) {
                    10.0.pow(floor(log10(maxValue)))
                } else {
                    1.0
                }
                val step = magnitude / 2.0
                val roundedMax = ceil(maxValue / step) * step
                
                LineChart(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    data = listOf(
                        Line(
                            values = normalizedIncomes,
                            color = SolidColor(FinanColors.OnIncome),
                            firstGradientFillColor = FinanColors.OnIncome.copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                            gradientAnimationDelay = 1000,
                            drawStyle = DrawStyle.Stroke(width = 1.dp),
                        ),
                        Line(
                            values = normalizedExpenses,
                            color = SolidColor(FinanColors.OnExpense),
                            firstGradientFillColor = FinanColors.OnExpense.copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                            gradientAnimationDelay = 1000,
                            drawStyle = DrawStyle.Stroke(width = 1.dp),
                            curvedEdges = true
                        )
                    ),
                    animationMode = AnimationMode.Together(delayBuilder = {
                        it * 500L
                    }),
                    minValue = 0.0,
                    maxValue = roundedMax,
                    dividerProperties = DividerProperties(enabled = false),
                    gridProperties = GridProperties(enabled = false),
                    zeroLineProperties = ZeroLineProperties().copy(enabled = true)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FinanBuddyTheme {
        HomeScreen(
            state = HomeState(
                isLoadingTransactions = true,
                transactionsExpensesByMonth = listOf(32.2, 300.1)
            ),
            onNavigation = {},
            onAction = {},
            currentRoute = Route.Home,
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenDarkPreview() {
    FinanBuddyTheme {
        HomeScreen(
            state = HomeState(
                isLoadingTransactions = true,
                transactionsIncomesByMonth = listOf(
                    200.0,
                    500.0,
                    1000.0,
                    700.0,
                    300.0,
                    400.0,
                    600.0,
                    800.0,
                    900.0,
                    1100.0,
                    1200.0,
                    1500.0,
                ),
                transactionsExpensesByMonth = listOf(
                    200.0,
                    500.0,
                    1000.0,
                    700.0,
                    300.0,
                    400.0,
                    600.0,
                    800.0,
                    900.0,
                    1100.0,
                    1200.0,
                    1500.0,
                ),
            ),
            onNavigation = {},
            onAction = {},
            currentRoute = Route.Home,
            onLogout = {}
        )
    }
}