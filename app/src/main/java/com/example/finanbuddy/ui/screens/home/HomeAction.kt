package com.example.finanbuddy.ui.screens.home

sealed interface HomeAction {
    object OnRefresh: HomeAction
}