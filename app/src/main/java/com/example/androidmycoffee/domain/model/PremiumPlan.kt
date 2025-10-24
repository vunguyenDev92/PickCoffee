package com.example.androidmycoffee.domain.model

data class PremiumPlan(
    val id: String = "premium_upgrade",
    val name: String = "Premium Upgrade",
    val description: String = "Unlock unlimited cart items and premium features",
    val price: String = "$4.99",
    val maxItems: Int = Int.MAX_VALUE,
    val isPurchased: Boolean = false,
)
