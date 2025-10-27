package com.example.androidmycoffee.presentation.ad

import android.app.Activity
import android.util.Log
import com.example.androidmycoffee.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading = false

    fun loadAd(activity: Activity) {
        if (adIsLoading || interstitialAd != null) {
            return
        }
        adIsLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("AdMobInter", "Interstitial loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("AdMobInter", "Interstitial failed to load: ${error.message}")
                    interstitialAd = null
                    adIsLoading = false
                }
            },
        )
    }

    fun showInterstitial(activity: Activity, onAdClosed: () -> Unit) {
        if (interstitialAd != null) {
            // [START set_fullscreen_callback]
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("AdMobInter", "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                        onAdClosed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Called when fullscreen content failed to show.
                        Log.d("AdMobInter", "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        interstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        Log.d("AdMobInter", "Ad showed fullscreen content.")
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d("AdMobInter", "Ad recorded an impression.")
                    }

                    override fun onAdClicked() {
                        // Called when ad is clicked.
                        Log.d("AdMobInter", "Ad was clicked.")
                    }
                }
            interstitialAd?.show(activity)
        } else {
            Log.d("AdMobInter", "Interstitial not ready, navigate immediately.")
            onAdClosed()
            loadAd(activity)
        }
    }
}
