package com.example.androidmycoffee.data.datasource.local

import com.example.androidmycoffee.data.datasource.local.entity.CoffeeEntity

interface CoffeeLocalDataSource {
    suspend fun getAllCoffees(): List<CoffeeEntity>
    suspend fun insertCoffees(list: List<CoffeeEntity>)
}
