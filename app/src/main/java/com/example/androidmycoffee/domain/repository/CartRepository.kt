package com.example.androidmycoffee.domain.repository

import com.example.androidmycoffee.domain.model.CartItem
import com.example.androidmycoffee.domain.model.PremiumPlan

interface CartRepository {
    fun getCartItems(): List<CartItem>
    fun addItem(item: CartItem)
    fun getCurrentPremiumPlan(): PremiumPlan?
}
