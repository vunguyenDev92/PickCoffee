package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.domain.repository.AuthRepository

class CheckAuthStatusUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Boolean {
        return authRepository.isSignedIn()
    }
}
