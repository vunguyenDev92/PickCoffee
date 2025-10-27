package com.example.androidmycoffee.domain.usecase

import android.content.Intent
import android.content.IntentSender
import com.example.androidmycoffee.data.auth.AuthResult
import com.example.androidmycoffee.domain.repository.AuthRepository

class SignInWithGoogleUseCase(
    private val authRepository: AuthRepository,
) {
    suspend fun beginSignIn(): IntentSender? {
        return authRepository.signInWithGoogle()
    }

    suspend fun signInWithIntent(intent: Intent): AuthResult {
        return authRepository.signInWithIntent(intent)
    }
}
