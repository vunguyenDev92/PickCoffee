// domain/repository/UserRepository.kt
package com.example.androidmycoffee.domain.repository

interface UserRepository {
    suspend fun isPremiumUser(): Boolean
    suspend fun setPremiumUser(isPremium: Boolean)
}
