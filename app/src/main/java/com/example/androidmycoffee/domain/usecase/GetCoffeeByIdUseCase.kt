package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.domain.repository.CoffeeRepository

class GetCoffeeByIdUseCase(
    private val repository: CoffeeRepository,
) {
    suspend operator fun invoke(id: Int): Coffee? {
        return repository.getCoffeeById(id)
    }
}
