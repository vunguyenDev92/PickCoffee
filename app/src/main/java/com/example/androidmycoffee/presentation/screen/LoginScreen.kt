package com.example.androidmycoffee.presentation.screen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidmycoffee.data.auth.FirebaseAuthDataSource
import com.example.androidmycoffee.domain.model.AuthState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun AuthScreen(
    authDataSource: FirebaseAuthDataSource,
    onSignInSuccess: () -> Unit,
) {
    val activity = LocalContext.current as Activity
    val authState by authDataSource.authState.collectAsState()
    var showError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)

            account.idToken?.let { idToken ->
                scope.launch {
                    val signInResult = authDataSource.signInWithGoogle(idToken)
                    if (signInResult.isSuccess) {
                        onSignInSuccess()
                    } else {
                        showError = signInResult.exceptionOrNull()?.message ?: "Sign in failed"
                    }
                }
            }
        } catch (e: ApiException) {
            showError = "Google sign in failed: ${e.message}"
        }
    }

    fun startGoogleSignIn() {
        val googleSignInClient = authDataSource.getGoogleSignInClient()
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = "☕ Welcome to Coffee Shop",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Sign in to enjoy your favorite coffee and exclusive features",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator()
                    Text("Signing in...", fontSize = 14.sp)
                }

                is AuthState.Authenticated -> {
                    LaunchedEffect(Unit) {
                        onSignInSuccess()
                    }
                }

                is AuthState.NotAuthenticated -> {
                    Button(
                        onClick = { startGoogleSignIn() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Google",
                            tint = Color.Black,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            "Sign in with Google",
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                    }
                }

                is AuthState.Error -> {
                    Text(
                        "Error: ${(authState as AuthState.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                    )

                    Button(
                        onClick = { startGoogleSignIn() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    ) {
                        Text("Try Again")
                    }
                }
            }

            if (showError != null) {
                Text(
                    text = showError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }
}
