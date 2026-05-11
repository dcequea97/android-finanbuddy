package com.example.finanbuddy.ui.screens.settings

sealed interface SettingsAction {
    data class SetSheetsUrl(val value: String) : SettingsAction
    data object SaveSettings : SettingsAction
    data object RetryLoad : SettingsAction
    data object ConsumeMessages : SettingsAction
}

