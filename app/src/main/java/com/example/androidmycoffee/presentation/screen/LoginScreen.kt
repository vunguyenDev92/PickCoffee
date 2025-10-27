// presentation/screen/LoginScreen.kt
package com.example.androidmycoffee.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.androidmycoffee.R
import com.example.androidmycoffee.data.auth.AuthResult
import com.example.androidmycoffee.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                viewModel.signInWithIntent(intent)
            }
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.Success -> {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            is AuthResult.Error -> {
                val message = (authState as AuthResult.Error).message
                Toast.makeText(context, "Login failed: $message", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Coffee Animation
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.coffee),
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Welcome to",
                fontSize = 24.sp,
                color = Color.Gray,
            )
            Text(
                text = "My Coffee Shop",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sign in to order your favorite coffee",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Google Sign-In Button
            Button(
                onClick = {
                    viewModel.signIn { intentSender ->
                        launcher.launch(
                            IntentSenderRequest.Builder(intentSender).build(),
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                ),
                enabled = authState !is AuthResult.Loading,
            ) {
                if (authState is AuthResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Google Icon (you need to add this drawable)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skip button (for testing)
            TextButton(
                onClick = { /* TODO: Skip login for testing */ },
                enabled = authState !is AuthResult.Loading,
            ) {
                Text("Continue without login")
            }
        }
    }
}
