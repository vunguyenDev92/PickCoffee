package com.example.androidmycoffee.domain.usecase

class CheckPremiumStatusUseCase {
    private var premium = false

    suspend operator fun invoke(): Boolean = premium

    fun setPremium(value: Boolean) {
        premium = value
    }
}
