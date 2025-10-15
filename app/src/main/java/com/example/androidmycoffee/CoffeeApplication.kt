package com.example.androidmycoffee

import android.app.Application
import com.google.android.gms.ads.MobileAds

class CoffeeApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
        MobileAds.initialize(this)
    }
}
