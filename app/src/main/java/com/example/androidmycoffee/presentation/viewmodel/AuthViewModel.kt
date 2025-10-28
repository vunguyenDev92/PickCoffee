package com.example.androidmycoffee.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidmycoffee.data.auth.FirebaseAuthDataSource
import com.example.androidmycoffee.domain.model.AuthState
import com.example.androidmycoffee.domain.model.User
import com.example.androidmycoffee.domain.usecase.SaveUserProfileUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authDataSource: FirebaseAuthDataSource,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
) : ViewModel() {

    val authState: StateFlow<AuthState> = authDataSource.authState

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            val result = authDataSource.signInWithGoogle(idToken)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    saveUserProfileUseCase(user)
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authDataSource.signOut()
        }
    }

    fun getCurrentUser(): User? {
        return authDataSource.getCurrentUser()
    }

    fun isSignedIn(): Boolean {
        return authDataSource.isSignedIn()
    }
}
