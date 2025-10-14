package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.domain.repository.CoffeeRepository

class GetCoffeeListUseCase(
    private val repository: CoffeeRepository,
) {
    suspend operator fun invoke(): List<Coffee> {
        return repository.getCoffeeList()
    }
}
