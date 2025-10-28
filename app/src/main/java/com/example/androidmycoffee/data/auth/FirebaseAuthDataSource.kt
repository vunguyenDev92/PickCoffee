package com.example.androidmycoffee.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.androidmycoffee.R
import com.example.androidmycoffee.domain.model.AuthState
import com.example.androidmycoffee.domain.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val context: Context,
    activity: Activity,
) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(
                User(
                    uid = currentUser.uid,
                    email = currentUser.email,
                    displayName = currentUser.displayName,
                    photoUrl = currentUser.photoUrl?.toString(),
                    isPremium = false,
                ),
            )
        } else {
            _authState.value = AuthState.NotAuthenticated
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            _authState.value = AuthState.Loading

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    isPremium = false,
                )
                _authState.value = AuthState.Authenticated(user)
                Result.success(user)
            } else {
                throw Exception("User is null after sign in")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error signing in with Google", e)
            _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            googleSignInClient.signOut().await()
            _authState.value = AuthState.NotAuthenticated
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out", e)
            _authState.value = AuthState.Error(e.message ?: "Sign out failed")
        }
    }

    fun getCurrentUser(): User? {
        return (authState.value as? AuthState.Authenticated)?.user
    }

    fun isSignedIn(): Boolean {
        return authState.value is AuthState.Authenticated
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleSignInClient
    }

    companion object {
        private const val TAG = "FirebaseAuthDataSource"
    }
}
