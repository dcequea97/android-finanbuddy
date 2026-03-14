package com.example.finanbuddy.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.domain.data.onError
import com.example.finanbuddy.domain.data.onFinally
import com.example.finanbuddy.domain.data.onSuccess
import com.example.finanbuddy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LoginState())

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.SetEmail -> {
                _state.update { it.copy(email = action.email, errorMessage = null) }
            }

            is LoginAction.SetPassword -> {
                _state.update { it.copy(password = action.password, errorMessage = null) }
            }

            is LoginAction.SignInWithGoogleToken -> {
                if (action.idToken.isBlank()) {
                    _state.update { it.copy(errorMessage = "Google token is invalid") }
                    return
                }
                authenticate { authRepository.signInWithGoogle(action.idToken) }
            }

            LoginAction.SignInWithEmail -> {
                val credentialsError = validateCredentials()
                if (credentialsError != null) {
                    _state.update { it.copy(errorMessage = credentialsError) }
                    return
                }

                val currentState = _state.value
                authenticate {
                    authRepository.signIn(
                        email = currentState.email.trim(),
                        password = currentState.password
                    )
                }
            }

            LoginAction.RegisterWithEmail -> {
                val credentialsError = validateCredentials()
                if (credentialsError != null) {
                    _state.update { it.copy(errorMessage = credentialsError) }
                    return
                }

                val currentState = _state.value
                authenticate {
                    authRepository.register(
                        email = currentState.email.trim(),
                        password = currentState.password
                    )
                }
            }

            is LoginAction.ShowError -> {
                _state.update { it.copy(errorMessage = action.message) }
            }

            LoginAction.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }

            LoginAction.AuthNavigationConsumed -> {
                _state.update { it.copy(isAuthenticated = false) }
            }
        }
    }

    private fun authenticate(authCall: suspend () -> com.example.finanbuddy.domain.data.Resource<Unit>) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authCall()
                .onSuccess {
                    _state.update { state -> state.copy(isAuthenticated = true) }
                }
                .onError { message ->
                    _state.update {
                        it.copy(
                            errorMessage = message.ifBlank { "Authentication failed" },
                            isAuthenticated = false
                        )
                    }
                }
                .onFinally {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun validateCredentials(): String? {
        val email = _state.value.email.trim()
        val password = _state.value.password

        if (email.isBlank() || password.isBlank()) return "Email and password are required"
        if (!email.contains("@")) return "Enter a valid email"
        if (password.length < 6) return "Password must have at least 6 characters"

        return null
    }
}

