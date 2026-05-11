package com.example.finanbuddy.ui.screens.settings

data class SettingsState(
    val sheetsUrl: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

