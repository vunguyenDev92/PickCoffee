package com.example.androidmycoffee.domain.repository

import com.example.androidmycoffee.domain.model.Coffee

interface CoffeeRepository {
    suspend fun getCoffeeList(): List<Coffee>
}
