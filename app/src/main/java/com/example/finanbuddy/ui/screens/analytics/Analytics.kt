package com.example.finanbuddy.ui.screens.analytics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanbuddy.R
import com.example.finanbuddy.ui.theme.FinanBuddyTheme

@Composable
fun AnalyticsRoot(
    viewModel: AnalyticsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AnalyticsScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun AnalyticsScreen(
    state: AnalyticsState,
    onAction: (AnalyticsAction) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(text = stringResource(R.string.analytics_coming_soon))

    }
}

@Preview
@Composable
private fun Preview() {
    FinanBuddyTheme {
        AnalyticsScreen(
            state = AnalyticsState(),
            onAction = {}
        )
    }
}