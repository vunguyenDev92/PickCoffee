package com.example.androidmycoffee.domain.model

data class CartItem(
    val coffee: Coffee,
    val quantity: Int = 1,
)
