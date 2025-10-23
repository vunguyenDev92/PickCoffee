package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.domain.model.CartItem
import com.example.androidmycoffee.domain.repository.CartRepository

class AddToCartUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(product: CartItem): Result<Unit> {
        val plan = repository.getCurrentPremiumPlan()
        val currentCart = repository.getCartItems()

        val limit = plan?.maxItems ?: 1
        return if (currentCart.size >= limit) {
            Result.failure(Exception("Need Premium"))
        } else {
            repository.addItem(product)
            Result.success(Unit)
        }
    }
}
