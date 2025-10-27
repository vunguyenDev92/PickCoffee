// presentation/viewmodel/AuthViewModel.kt
package com.example.androidmycoffee.presentation.viewmodel

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidmycoffee.data.auth.AuthResult
import com.example.androidmycoffee.domain.model.User
import com.example.androidmycoffee.domain.usecase.CheckAuthStatusUseCase
import com.example.androidmycoffee.domain.usecase.GetCurrentUserUseCase
import com.example.androidmycoffee.domain.usecase.SignInWithGoogleUseCase
import com.example.androidmycoffee.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.NotAuthenticated)
    val authState: StateFlow<AuthResult> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            if (checkAuthStatusUseCase()) {
                val user = getCurrentUserUseCase()
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthResult.Success(user)
                } else {
                    _authState.value = AuthResult.NotAuthenticated
                }
            } else {
                _authState.value = AuthResult.NotAuthenticated
            }
        }
    }

    fun signIn(onLaunch: (IntentSender) -> Unit) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading

            val intentSender = signInWithGoogleUseCase.beginSignIn()
            if (intentSender != null) {
                onLaunch(intentSender)
            } else {
                _authState.value = AuthResult.Error("Failed to start sign in")
            }
        }
    }

    fun signInWithIntent(intent: Intent) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading

            val result = signInWithGoogleUseCase.signInWithIntent(intent)
            _authState.value = result

            if (result is AuthResult.Success) {
                _currentUser.value = result.user
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _currentUser.value = null
            _authState.value = AuthResult.NotAuthenticated
        }
    }

    fun getCurrentUser(): User? {
        return _currentUser.value ?: getCurrentUserUseCase()
    }

    fun isSignedIn(): Boolean {
        return checkAuthStatusUseCase()
    }

    fun resetAuthState() {
        _authState.value = AuthResult.NotAuthenticated
    }
}
