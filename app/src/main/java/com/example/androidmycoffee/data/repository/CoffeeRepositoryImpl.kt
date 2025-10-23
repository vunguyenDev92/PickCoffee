package com.example.androidmycoffee.data.repository

import com.example.androidmycoffee.data.datasource.local.CoffeeLocalDataSource
import com.example.androidmycoffee.data.datasource.local.entity.toDomain
import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.domain.repository.CoffeeRepository

class CoffeeRepositoryImpl(private val coffeeLocalDataSource: CoffeeLocalDataSource) : CoffeeRepository {
    override suspend fun getCoffeeList(): List<Coffee> {
        return coffeeLocalDataSource.getAllCoffees().map { it.toDomain() }
    }
    
    override suspend fun getCoffeeById(id: Int): Coffee? {
        return coffeeLocalDataSource.getCoffeeById(id)?.toDomain()
    }
}
