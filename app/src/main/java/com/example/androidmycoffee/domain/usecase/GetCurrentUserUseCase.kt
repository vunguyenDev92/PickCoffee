package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.domain.model.User
import com.example.androidmycoffee.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}
