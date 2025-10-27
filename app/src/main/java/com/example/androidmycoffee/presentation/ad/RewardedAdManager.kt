package com.example.androidmycoffee.presentation.ad

import android.app.Activity
import android.util.Log
import com.example.androidmycoffee.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object RewardedAdManager {
    private const val TAG = "RewardedAdManager"
    private var rewardedAd: RewardedAd? = null
    private var adIsLoading = false

    fun loadAd(activity: Activity) {
        if (adIsLoading || rewardedAd != null) {
            return
        }
        adIsLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            activity,
            BuildConfig.ADMOB_REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    rewardedAd = ad
                    adIsLoading = false
                }
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "Rewarded ad failed to load: ${adError.message}")
                    rewardedAd = null
                    adIsLoading = false
                }
            },
        )
    }

    fun showRewardedAd(activity: Activity, onComplete: () -> Unit) {
        val ad = rewardedAd
        if (ad != null) {
            ad.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d(TAG, "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null
//                        loadAd(activity)
                        onComplete()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Called when fullscreen content failed to show.
                        Log.d(TAG, "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null
                        loadAd(activity)
                        onComplete()
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        Log.d(TAG, "Ad showed fullscreen content.")
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.")
                    }

                    override fun onAdClicked() {
                        // Called when an ad is clicked.
                        Log.d(TAG, "Ad was clicked.")
                        loadAd(activity)
                    }
                }
            ad.show(
                activity,
                OnUserEarnedRewardListener { rewardItem ->
                    Log.d(TAG, "User earned the reward.")
                    // Handle the reward.
                    onComplete()
                },
            )
        } else {
            Log.d(TAG, "Rewarded ad is not loaded, executing callback directly")
            onComplete()
        }
    }
    fun isAdLoaded(): Boolean {
        return rewardedAd != null
    }
}
