package com.example.finanbuddy.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey


fun NavBackStack<NavKey>.navigate(route: Route) {
    if (this.any { it == route }) {
        this.popTo(route)
        return
    }

    this.add(route)
}

fun NavBackStack<NavKey>.pop() {
    this.removeLastOrNull()
}

fun NavBackStack<NavKey>.popTo(route: Route) {
    while (this.isNotEmpty()) {
        val last = this.removeLastOrNull()
        if (last == route) break
    }
}

fun NavBackStack<NavKey>.popToRoot() {
    while (this.size > 1) {
        this.removeLastOrNull()
    }
}

fun NavBackStack<NavKey>.currentRoute(): Route {
    return this.lastOrNull() as Route
}

fun NavBackStack<NavKey>.navigateAndPopTo(
    route: Route,
    popTo: Route
) {
    this.popTo(popTo)
    this.add(route)
}