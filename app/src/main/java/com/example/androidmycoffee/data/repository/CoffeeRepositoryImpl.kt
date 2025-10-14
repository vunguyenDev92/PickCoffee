package com.example.androidmycoffee.data.repository

import com.example.androidmycoffee.data.datasource.local.CoffeeLocalDataSource
import com.example.androidmycoffee.data.datasource.local.entity.CoffeeEntity
import com.example.androidmycoffee.domain.repository.CoffeeRepository

class CoffeeRepositoryImpl(private val coffeeLocalDataSource: CoffeeLocalDataSource) : CoffeeRepository {
    override suspend fun getCoffeeList(): List<CoffeeEntity> {
        return coffeeLocalDataSource.getAllCoffees()
    }
}
