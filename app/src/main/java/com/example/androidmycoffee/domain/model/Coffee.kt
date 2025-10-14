package com.example.androidmycoffee.domain.model

import java.math.BigDecimal

data class Coffee(
    val id: Int,
    val name: String,
    val type: TypeCoffee,
    val price: BigDecimal,
)

enum class TypeCoffee {
    LATTE,
    CAPPUCCINO,
    ESPRESSO,
}
