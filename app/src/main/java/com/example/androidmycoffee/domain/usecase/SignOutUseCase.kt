package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.domain.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke() {
        authRepository.signOut()
    }
}
