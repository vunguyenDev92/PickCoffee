package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.data.datasource.local.entity.CoffeeEntity
import com.example.androidmycoffee.domain.repository.CoffeeRepository

class GetCoffeeListUseCase(
    private val repository: CoffeeRepository,
) {
    suspend operator fun invoke(): List<CoffeeEntity> {
        return repository.getCoffeeList()
    }
}
