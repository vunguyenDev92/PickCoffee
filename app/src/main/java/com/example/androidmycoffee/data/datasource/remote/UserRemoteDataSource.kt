package com.example.androidmycoffee.data.datasource.remote

import com.example.androidmycoffee.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface UserDataSource {
    suspend fun saveUserProfile(user: User): Result<Unit>
    suspend fun getUserProfile(uid: String): Result<User?>
    suspend fun deleteUserProfile(uid: String): Result<Unit>
    suspend fun updatePremiumStatus(uid: String, isPremium: Boolean): Result<Unit>
}

class UserRemoteDataSourceImpl(private val firestore: FirebaseFirestore) : UserDataSource {

    override suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            val userMap = mapOf(
                "uid" to user.uid,
                "email" to user.email,
                "displayName" to user.displayName,
                "photoUrl" to user.photoUrl,
                "isPremium" to user.isPremium,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis(),
            )

            firestore.collection("users")
                .document(user.uid)
                .set(userMap)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(uid: String): Result<User?> {
        return try {
            val document = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (document.exists()) {
                val user = User(
                    uid = document.getString("uid") ?: uid,
                    email = document.getString("email"),
                    displayName = document.getString("displayName"),
                    photoUrl = document.getString("photoUrl"),
                    isPremium = document.getBoolean("isPremium") ?: false,
                )
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUserProfile(uid: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(uid)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePremiumStatus(uid: String, isPremium: Boolean): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(uid)
                .update(
                    mapOf(
                        "isPremium" to isPremium,
                        "updatedAt" to System.currentTimeMillis(),
                    ),
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
