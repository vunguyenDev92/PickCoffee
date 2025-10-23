package com.example.androidmycoffee.presentation.screen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.androidmycoffee.R
import com.example.androidmycoffee.presentation.ad.InterstitialAdManager
import kotlinx.coroutines.delay

@SuppressLint("ContextCastToActivity")
@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    val activity = LocalContext.current as Activity
    LaunchedEffect(Unit) {
        delay(4000)
        // show inter ad
        InterstitialAdManager.showInterstitial(activity) {
            onNavigate()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F1F8)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 244.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coffee))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1,
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp),
            )
            Text(
                text = "AI Language Translate",
                fontSize = 28.sp,
                color = Color(0xFFF55025),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
