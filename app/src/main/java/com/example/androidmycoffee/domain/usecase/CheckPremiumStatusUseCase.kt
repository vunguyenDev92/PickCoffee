package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.data.billing.BillingManager
import com.example.androidmycoffee.data.billing.PurchaseState

class CheckPremiumStatusUseCase(
    private val billingManager: BillingManager,
) {
    operator fun invoke(): Boolean {
        return billingManager.purchaseState.value is PurchaseState.Purchased
    }

    suspend fun getPremiumStatus(): Boolean {
        return invoke()
    }
}
