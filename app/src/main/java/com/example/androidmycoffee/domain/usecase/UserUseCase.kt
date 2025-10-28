package com.example.androidmycoffee.domain.usecase

import com.example.androidmycoffee.domain.model.User
import com.example.androidmycoffee.domain.repository.UserRepository

class SaveUserProfileUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.saveUserProfile(user)
    }
}

class GetUserProfileUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String): Result<User?> {
        return userRepository.getUserProfile(uid)
    }
}

class DeleteUserProfileUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String): Result<Unit> {
        return userRepository.deleteUserProfile(uid)
    }
}

class CheckPremiumStatusUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String): Boolean {
        return userRepository.isPremiumUser(uid)
    }
}

class SetPremiumStatusUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String, isPremium: Boolean): Result<Unit> {
        return userRepository.setPremiumUser(uid, isPremium)
    }
}
