package com.example.androidmycoffee.data.repository

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.androidmycoffee.data.auth.AuthResult
import com.example.androidmycoffee.data.auth.GoogleAuthManager
import com.example.androidmycoffee.data.datasource.local.PreferencesManager
import com.example.androidmycoffee.domain.model.User
import com.example.androidmycoffee.domain.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val googleAuthManager: GoogleAuthManager,
    private val preferencesManager: PreferencesManager,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override suspend fun signInWithGoogle(): IntentSender? {
        return googleAuthManager.signIn()
    }

    override suspend fun signInWithIntent(intent: Intent): AuthResult {
        val result = googleAuthManager.signInWithIntent(intent)

        if (result is AuthResult.Success) {
            preferencesManager.saveUser(result.user)
            saveUserToFirestore(result.user)
        }

        return result
    }

    override fun getCurrentUser(): User? {
        val cachedUser = preferencesManager.getUser()
        if (cachedUser != null) {
            return cachedUser
        }

        return googleAuthManager.getCurrentUser()?.also { user ->
            preferencesManager.saveUser(user)
        }
    }

    override fun isSignedIn(): Boolean {
        return preferencesManager.isLoggedIn() && googleAuthManager.isSignedIn()
    }

    override fun signOut() {
        googleAuthManager.signOut()
        preferencesManager.clearSession()
    }

    private suspend fun saveUserToFirestore(user: User) {
        try {
            val userMap = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "displayName" to user.displayName,
                "photoUrl" to user.photoUrl,
                "isPremium" to user.isPremium,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis(),
            )

            firestore.collection("users")
                .document(user.uid)
                .set(userMap)
                .await()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to save user to Firestore", e)
        }
    }
}
