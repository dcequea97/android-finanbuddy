package com.example.finanbuddy.ui.screens.auth

import android.content.Intent

sealed interface LoginAction {
    data class SetEmail(val email: String) : LoginAction
    data class SetPassword(val password: String) : LoginAction
    data class HandleGoogleSignInResult(val data: Intent?) : LoginAction
    data class ShowError(val message: String) : LoginAction

    data object SignInWithEmail : LoginAction
    data object RegisterWithEmail : LoginAction
    data object ClearError : LoginAction
    data object AuthNavigationConsumed : LoginAction
}

