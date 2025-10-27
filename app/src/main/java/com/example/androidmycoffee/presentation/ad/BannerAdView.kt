package com.example.androidmycoffee.presentation.ad

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.androidmycoffee.BuildConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdView() {
    val context = LocalContext.current
    val adView = remember { AdView(context) }
    val adSize by remember { mutableStateOf(getAdSize(context as Activity)) }

    LaunchedEffect(Unit) {
        adView.adUnitId = BuildConfig.ADMOB_BANNER_AD_UNIT_ID
        adView.setAdSize(adSize)

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("AdMob", "Banner loaded successfully.")
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.e("AdMob", "Failed to load banner: ${p0.message}")
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(factory = { adView })
        }
    }
}

fun getAdSize(activity: Activity): AdSize {
    val display = activity.windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)

    val density = outMetrics.density
    val adWidthPixels = outMetrics.widthPixels.toFloat()
    val adWidth = (adWidthPixels / density).toInt()

    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
}
