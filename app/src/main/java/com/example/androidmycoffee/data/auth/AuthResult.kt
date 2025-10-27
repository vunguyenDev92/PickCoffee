package com.example.androidmycoffee.data.auth

sealed class AuthResult {
    data class Success(val user: com.example.androidmycoffee.domain.model.User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
    object NotAuthenticated : AuthResult()
}
