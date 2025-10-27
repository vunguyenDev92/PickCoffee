package com.example.androidmycoffee.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.example.androidmycoffee.data.auth.AuthResult
import com.example.androidmycoffee.domain.model.User

interface AuthRepository {
    suspend fun signInWithGoogle(): IntentSender?
    suspend fun signInWithIntent(intent: Intent): AuthResult
    fun getCurrentUser(): User?
    fun isSignedIn(): Boolean
    fun signOut()
}
