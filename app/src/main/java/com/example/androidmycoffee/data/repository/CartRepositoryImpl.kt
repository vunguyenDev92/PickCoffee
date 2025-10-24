package com.example.androidmycoffee.data.repository

import com.example.androidmycoffee.data.billing.BillingManager
import com.example.androidmycoffee.data.billing.PurchaseState
import com.example.androidmycoffee.domain.model.CartItem
import com.example.androidmycoffee.domain.model.PremiumPlan
import com.example.androidmycoffee.domain.repository.CartRepository

class CartRepositoryImpl(
    private val billingManager: BillingManager,
) : CartRepository {

    private val cart = mutableListOf<CartItem>()
    private var currentPlan: PremiumPlan? = null

    override fun getCartItems(): List<CartItem> = cart

    override fun addItem(item: CartItem) {
        cart.add(item)
    }

    override fun getCurrentPremiumPlan(): PremiumPlan {
        val isPremium = billingManager.purchaseState.value is PurchaseState.Purchased
        return if (isPremium) {
            PremiumPlan(isPurchased = true, maxItems = Int.MAX_VALUE)
        } else {
            PremiumPlan(isPurchased = false, maxItems = 1)
        }
    }

    fun setPremiumPlan(plan: PremiumPlan) {
        currentPlan = plan
    }
}
