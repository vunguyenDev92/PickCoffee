package com.example.androidmycoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.androidmycoffee.presentation.ad.InterstitialAdManager
import com.example.androidmycoffee.presentation.ad.RewardedAdManager
import com.example.androidmycoffee.presentation.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        InterstitialAdManager.loadAd(this)
        RewardedAdManager.loadAd(this)

        val appContainer = (application as CoffeeApplication).appContainer

        appContainer.initializeAuth(this)

        setContent {
            val navController = rememberNavController()
            AppNavGraph(navController = navController, appContainer = appContainer)
        }
    }
}
