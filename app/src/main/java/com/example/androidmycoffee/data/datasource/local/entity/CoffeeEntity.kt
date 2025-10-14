package com.example.androidmycoffee.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidmycoffee.domain.model.TypeCoffee
import java.math.BigDecimal

@Entity(tableName = "coffee")
data class CoffeeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: TypeCoffee,
    val price: BigDecimal,
)
