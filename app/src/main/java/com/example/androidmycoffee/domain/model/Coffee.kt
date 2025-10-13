package com.example.androidmycoffee.domain.model

data class Coffee(
    val id: Int,
    val name: String,
    val type: TypeCoffee,
    val price: Int,
)

enum class TypeCoffee {
    LATTE,
    CAPPUCCINO,
    ESPRESSO,
}
