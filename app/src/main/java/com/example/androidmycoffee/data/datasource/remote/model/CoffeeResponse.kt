package com.example.androidmycoffee.data.datasource.remote.model

import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.domain.model.TypeCoffee
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CoffeeResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("isAvailable")
    val isAvailable: Boolean = true
)

fun CoffeeResponse.toDomain(): Coffee {
    return Coffee(
        id = this.id,
        name = this.name,
        type = TypeCoffee.valueOf(this.type.uppercase()),
        price = BigDecimal(this.price),
    )
}

data class OrderRequest(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("items")
    val items: List<OrderItemRequest>,

    @SerializedName("totalPrice")
    val totalPrice: Double
)

data class OrderItemRequest(
    @SerializedName("coffeeId")
    val coffeeId: Int,

    @SerializedName("coffeeName")
    val coffeeName: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("price")
    val price: Double
)

data class OrderResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("items")
    val items: List<OrderItemResponse>,

    @SerializedName("totalPrice")
    val totalPrice: Double,

    @SerializedName("status")
    val status: String,

    @SerializedName("createdAt")
    val createdAt: Long,

    @SerializedName("updatedAt")
    val updatedAt: Long
)

data class OrderItemResponse(
    @SerializedName("coffeeId")
    val coffeeId: Int,

    @SerializedName("coffeeName")
    val coffeeName: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("price")
    val price: Double
)

data class StatusUpdateRequest(
    @SerializedName("status")
    val status: String
)

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null
)
