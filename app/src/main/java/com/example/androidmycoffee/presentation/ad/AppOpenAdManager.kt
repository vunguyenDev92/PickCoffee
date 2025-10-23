package com.example.androidmycoffee.presentation.ad

import android.app.Activity
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

class AppOpenAdManager(private val application: Application) {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
        private const val TAG = "AppOpenAd"
    }

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            application,
            "AD_UNIT_ID",
            AdRequest.Builder().build(),
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    // Called when an app open ad has loaded.
                    Log.d(TAG, "App open ad loaded.")

                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Called when an app open ad has failed to load.
                    Log.d(TAG, "App open ad failed to load with error: " + loadAdError.message)

                    isLoadingAd = false
                }
            },
        )
    }
    fun showAdIfAvailable(activity: Activity, onAdDismissed: () -> Unit) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.d(TAG, "The app open ad is already showing.")
            return
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (appOpenAd == null) {
            Log.d(TAG, "The app open ad is not ready yet.")
            onAdDismissed()
            loadAd()
            return
        }

        appOpenAd?.fullScreenContentCallback =
            object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when full screen content is dismissed.
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    appOpenAd = null
                    isShowingAd = false
                    onAdDismissed()
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when full screen content failed to show.
                    Log.d(TAG, adError.message)
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    appOpenAd = null
                    isShowingAd = false
                    onAdDismissed()
                    loadAd()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "The ad recorded an impression.")
                }

                override fun onAdClicked() {
                    // Called when ad is clicked.
                    Log.d(TAG, "The ad was clicked.")
                }
            }

        isShowingAd = true
        appOpenAd?.show(activity)
    }
}
