package com.example.finanbuddy.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.ui.components.AppLoader
import com.example.finanbuddy.ui.navigation.AppScaffold
import com.example.finanbuddy.ui.navigation.Route
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoot(
    onNavigation: (Route) -> Unit,
    currentRoute: Route,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigation = onNavigation,
        currentRoute = currentRoute,
        onLogout = onLogout
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigation: (Route) -> Unit,
    currentRoute: Route,
    onLogout: () -> Unit
) {
    AppScaffold(
        onNavigation = onNavigation,
        onLogout = onLogout,
        currentRoute = currentRoute,
        showBottomBar = true,
        showHeader = true
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Configure the Sheets endpoint used by the app.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.sheetsUrl,
                    onValueChange = { onAction(SettingsAction.SetSheetsUrl(it)) },
                    label = { Text("Sheets Url") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    enabled = !state.isSaving && !state.isLoading
                )

                Button(
                    onClick = { onAction(SettingsAction.SaveSettings) },
                    enabled = !state.isSaving && !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.isSaving) "Saving..." else "Save")
                }

                if (state.isLoading) {
                    AppLoader()
                }

                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                state.successMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

