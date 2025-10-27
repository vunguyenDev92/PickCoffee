package com.example.androidmycoffee.data.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.androidmycoffee.domain.model.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) {

    private val auth: FirebaseAuth = Firebase.auth
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    private val webClientId = "AIzaSyCnXs9dZAV7zqjtrV-L-YCF4FmYGYI0hWQ"

    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(webClientId)
                .setFilterByAuthorizedAccounts(false)
                .build(),
        )
        .setAutoSelectEnabled(true)
        .build()

    suspend fun signIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(signInRequest).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Sign in failed", e)
            null
        }
    }

    suspend fun signInWithIntent(intent: Intent): AuthResult {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken

            if (googleIdToken != null) {
                val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(googleCredentials).await()

                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val user = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email,
                        displayName = firebaseUser.displayName,
                        photoUrl = firebaseUser.photoUrl?.toString(),
                        isPremium = false,
                    )
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Authentication failed")
                }
            } else {
                AuthResult.Error("No ID token")
            }
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Sign in with intent failed", e)
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    fun getCurrentUser(): com.example.androidmycoffee.domain.model.User? {
        val firebaseUser = auth.currentUser ?: return null
        return com.example.androidmycoffee.domain.model.User(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            photoUrl = firebaseUser.photoUrl?.toString(),
            isPremium = false,
        )
    }

    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
        oneTapClient.signOut()
    }
}
