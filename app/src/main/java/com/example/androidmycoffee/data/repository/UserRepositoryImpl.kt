package com.example.androidmycoffee.data.repository

import com.example.androidmycoffee.data.datasource.remote.UserDataSource
import com.example.androidmycoffee.domain.model.User
import com.example.androidmycoffee.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
) : UserRepository {

    override suspend fun saveUserProfile(user: User): Result<Unit> {
        return userDataSource.saveUserProfile(user)
    }

    override suspend fun getUserProfile(uid: String): Result<User?> {
        return userDataSource.getUserProfile(uid)
    }

    override suspend fun deleteUserProfile(uid: String): Result<Unit> {
        return userDataSource.deleteUserProfile(uid)
    }

    override suspend fun isPremiumUser(uid: String): Boolean {
        return try {
            val result = userDataSource.getUserProfile(uid)
            result.getOrNull()?.isPremium ?: false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun setPremiumUser(uid: String, isPremium: Boolean): Result<Unit> {
        return userDataSource.updatePremiumStatus(uid, isPremium)
    }
}
