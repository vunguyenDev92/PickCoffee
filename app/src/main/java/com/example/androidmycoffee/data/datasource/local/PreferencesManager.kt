package com.example.androidmycoffee.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.androidmycoffee.domain.model.User
import com.google.gson.Gson

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "coffee_prefs",
        Context.MODE_PRIVATE,
    )

    private val gson = Gson()

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_PHOTO_URL = "photo_url"
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    fun saveUser(user: User) {
        prefs.edit().apply {
            putString(KEY_USER, gson.toJson(user))
            putString(KEY_USER_ID, user.uid)
            putString(KEY_EMAIL, user.email)
            putString(KEY_DISPLAY_NAME, user.displayName)
            putString(KEY_PHOTO_URL, user.photoUrl)
            putBoolean(KEY_IS_PREMIUM, user.isPremium)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setPremiumStatus(isPremium: Boolean) {
        prefs.edit { putBoolean(KEY_IS_PREMIUM, isPremium) }

        getUser()?.let { user ->
            saveUser(user.copy(isPremium = isPremium))
        }
    }

    fun isPremium(): Boolean {
        return prefs.getBoolean(KEY_IS_PREMIUM, false)
    }

    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_USER)
            remove(KEY_USER_ID)
            remove(KEY_EMAIL)
            remove(KEY_DISPLAY_NAME)
            remove(KEY_PHOTO_URL)
            putBoolean(KEY_IS_LOGGED_IN, false)
            // Keep IS_PREMIUM for trial tracking
            apply()
        }
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        prefs.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}
