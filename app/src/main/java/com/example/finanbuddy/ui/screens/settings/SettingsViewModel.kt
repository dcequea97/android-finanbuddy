package com.example.finanbuddy.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.domain.data.Resource
import com.example.finanbuddy.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
        .onStart {
            if (!hasLoadedInitialData) {
                loadSettings()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SettingsState()
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.ConsumeMessages -> {
                _state.update { it.copy(errorMessage = null, successMessage = null) }
            }

            SettingsAction.RetryLoad -> loadSettings()
            SettingsAction.SaveSettings -> saveSettings()
            is SettingsAction.SetSheetsUrl -> {
                _state.update {
                    it.copy(
                        sheetsUrl = action.value,
                        errorMessage = null,
                        successMessage = null
                    )
                }
            }
        }
    }

    private fun loadSettings() {
        _state.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            when (val result = settingsRepository.getSheetsUrl()) {
                is Resource.Success -> _state.update {
                    it.copy(sheetsUrl = result.data, isLoading = false)
                }

                is Resource.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.message.ifBlank { "Could not load settings" }
                    )
                }
            }
        }
    }

    private fun saveSettings() {
        val url = _state.value.sheetsUrl.trim()
        if (url.isBlank()) {
            _state.update { it.copy(errorMessage = "Sheets Url is required") }
            return
        }

        _state.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            when (val result = settingsRepository.saveSheetsUrl(url)) {
                is Resource.Success -> _state.update {
                    it.copy(
                        sheetsUrl = url,
                        isSaving = false,
                        successMessage = "Sheets Url saved"
                    )
                }

                is Resource.Error -> _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = result.message.ifBlank { "Could not save settings" }
                    )
                }
            }
        }
    }
}

