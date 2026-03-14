package com.example.finanbuddy.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finanbuddy.ui.theme.FinanBuddyTheme

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    currentRoute: Route? = null,
    onNavigation: (Route) -> Unit = {},
    showBottomBar: Boolean = false,
    showHeader: Boolean = false,
    showActionButton: Boolean = false,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            if (showHeader) {
                Header()
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
                    onClick = { onNavigation(Route.AddExpense) },
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
}


@Composable
private fun Header() {
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding, start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Overview",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                // simple bell placeholder
                Icon(
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    imageVector = Icons.Rounded.Notifications
                )
            }
            // Placeholder avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            ) {
                // Keep placeholder simple: colored box
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {}
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
        NavigationItem(
            route = Route.Home,
            label = "Home",
            icon = Icons.Rounded.Dashboard
        ),
        NavigationItem(
            route = Route.Analytics,
            label = "Analytics",
            icon = Icons.Rounded.Analytics
        ),
        NavigationItem(
            route = Route.Settings,
            label = "Settings",
            icon = Icons.Rounded.Settings
        )
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
            contentDescription = "Add",
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
        Header()
    }
}