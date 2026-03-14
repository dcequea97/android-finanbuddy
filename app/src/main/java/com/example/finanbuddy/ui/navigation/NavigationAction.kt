package com.example.finanbuddy.ui.navigation

sealed interface NavigationAction {
    data object Pop : NavigationAction
    data object PopToRoot : NavigationAction
    data class Navigate(val route: Route) : NavigationAction
    data class PopTo(val route: Route) : NavigationAction
    data class NavigateAndPopTo(val route: Route, val popTo: Route) : NavigationAction
}
