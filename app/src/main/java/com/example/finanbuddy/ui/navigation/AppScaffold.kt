package com.example.finanbuddy.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finanbuddy.R
import com.example.finanbuddy.ui.theme.FinanBuddyTheme

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    currentRoute: Route? = null,
    onNavigation: (Route) -> Unit = {},
    onLogout: () -> Unit = {},
    showBottomBar: Boolean = false,
    showHeader: Boolean = false,
    showActionButton: Boolean = false,
    headerTitle: String = stringResource(R.string.overview),
    content: @Composable () -> Unit
) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (showHeader) {
                Header(
                    onLogout = { showLogoutDialog = true },
                    title = headerTitle
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    onNavigation = { route -> onNavigation(route) },
                    currentRoute = currentRoute
                )
            }
        },
        floatingActionButton = {
            if (showActionButton) {
                AddFloatingActionButton(
                    onClick = { onNavigation(Route.AddExpense()) },
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                content()
            }
        }
    )

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = stringResource(R.string.dialog_logout_title)) },
            text = { Text(text = stringResource(R.string.dialog_logout_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(text = stringResource(R.string.btn_logout))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}


@Composable
private fun Header(
    onLogout: () -> Unit,
    title: String = stringResource(R.string.overview)
) {
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding, start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            IconButton(onClick = { }) {
//                // simple bell placeholder
//                Icon(
//                    contentDescription = "Notifications",
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                    imageVector = Icons.Rounded.Notifications
//                )
//            }
            IconButton(onClick = onLogout) {
                Icon(
                    contentDescription = stringResource(R.string.cd_logout),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    imageVector = Icons.Rounded.Logout
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    onNavigation: (Route) -> Unit,
    currentRoute: Route?
) {
    data class NavigationItem(
        val route: Route,
        val label: String,
        val icon: ImageVector
    )

    val navigationItems = listOf(
        NavigationItem(route = Route.Home, label = stringResource(R.string.nav_home), icon = Icons.Rounded.Dashboard),
        NavigationItem(route = Route.Analytics, label = stringResource(R.string.nav_analytics), icon = Icons.Rounded.Analytics),
        NavigationItem(route = Route.Settings, label = stringResource(R.string.nav_settings), icon = Icons.Rounded.Settings)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        navigationItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = { onNavigation(navItem.route) },
                icon = {
                    Icon(
                        contentDescription = navItem.label,
                        tint = MaterialTheme.colorScheme.primary,
                        imageVector = navItem.icon
                    )
                },
                label = {
                    Text(
                        navItem.label,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

@Composable
private fun AddFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(
            // simple plus placeholder
            contentDescription = stringResource(R.string.cd_add),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            imageVector = Icons.Rounded.Add
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun BottomBarPreview() {
    FinanBuddyTheme {
        BottomBar(onNavigation = {}, currentRoute = Route.Home)
    }
}

@Composable
@Preview(showBackground = true)
private fun HeaderPreview() {
    FinanBuddyTheme {
        Header(onLogout = {})
    }
}