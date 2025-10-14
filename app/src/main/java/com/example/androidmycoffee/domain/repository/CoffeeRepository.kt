package com.example.androidmycoffee.domain.repository

import com.example.androidmycoffee.data.datasource.local.entity.CoffeeEntity

interface CoffeeRepository {
    suspend fun getCoffeeList(): List<CoffeeEntity>
}
