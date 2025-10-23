package com.example.androidmycoffee.data.repository

import com.example.androidmycoffee.domain.model.CartItem
import com.example.androidmycoffee.domain.model.PremiumPlan
import com.example.androidmycoffee.domain.repository.CartRepository

class CartRepositoryImpl : CartRepository {

    private val cart = mutableListOf<CartItem>()
    private var currentPlan: PremiumPlan? = null

    override fun getCartItems(): List<CartItem> = cart

    override fun addItem(item: CartItem) {
        cart.add(item)
    }

    override fun getCurrentPremiumPlan(): PremiumPlan? = currentPlan

    fun setPremiumPlan(plan: PremiumPlan) {
        currentPlan = plan
    }
}
