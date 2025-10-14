package com.example.androidmycoffee.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.domain.model.TypeCoffee
import java.math.BigDecimal

@Entity(tableName = "coffee")
data class CoffeeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: TypeCoffee,
    val price: BigDecimal,
)

fun CoffeeEntity.toDomain(): Coffee = Coffee(
    id = this.id,
    name = this.name,
    type = this.type,
    price = this.price,
)
