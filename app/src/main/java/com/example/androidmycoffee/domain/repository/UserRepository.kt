package com.example.androidmycoffee.domain.repository

import com.example.androidmycoffee.domain.model.User

interface UserRepository {
    suspend fun saveUserProfile(user: User): Result<Unit>
    suspend fun getUserProfile(uid: String): Result<User?>
    suspend fun deleteUserProfile(uid: String): Result<Unit>
    suspend fun isPremiumUser(uid: String): Boolean
    suspend fun setPremiumUser(uid: String, isPremium: Boolean): Result<Unit>
}
