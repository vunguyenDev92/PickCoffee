package com.example.androidmycoffee

import android.app.Activity
import android.content.Context
import com.example.androidmycoffee.data.auth.FirebaseAuthDataSource
import com.example.androidmycoffee.data.billing.BillingManager
import com.example.androidmycoffee.data.datasource.local.CoffeeLocalDataSourceImpl
import com.example.androidmycoffee.data.datasource.local.database.AppDatabase
import com.example.androidmycoffee.data.datasource.remote.UserRemoteDataSourceImpl
import com.example.androidmycoffee.data.repository.CartRepositoryImpl
import com.example.androidmycoffee.data.repository.CoffeeRepositoryImpl
import com.example.androidmycoffee.data.repository.UserRepositoryImpl
import com.example.androidmycoffee.domain.usecase.AddToCartUseCase
import com.example.androidmycoffee.domain.usecase.CheckPremiumStatusUseCase
import com.example.androidmycoffee.domain.usecase.DeleteUserProfileUseCase
import com.example.androidmycoffee.domain.usecase.GetCoffeeByIdUseCase
import com.example.androidmycoffee.domain.usecase.GetCoffeeListUseCase
import com.example.androidmycoffee.domain.usecase.GetUserProfileUseCase
import com.example.androidmycoffee.domain.usecase.SaveUserProfileUseCase
import com.google.firebase.firestore.FirebaseFirestore

class AppContainer(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val localDataSource = CoffeeLocalDataSourceImpl(db.coffeeDao())

    private val firestore = FirebaseFirestore.getInstance()

    private val coffeeRepository = CoffeeRepositoryImpl(localDataSource)
    private val userRemoteDataSource = UserRemoteDataSourceImpl(firestore)
    val userRepository = UserRepositoryImpl(userRemoteDataSource)

    private var _authDataSource: FirebaseAuthDataSource? = null
    val authDataSource: FirebaseAuthDataSource
        get() = _authDataSource ?: throw IllegalStateException(
            "AuthDataSource not initialized. Call initializeAuth() from MainActivity first.",
        )

    val billingManager = BillingManager(context).apply { initialize() }
    val cartRepository = CartRepositoryImpl(billingManager)

    val getCoffeeListUseCase = GetCoffeeListUseCase(coffeeRepository)
    val getCoffeeByIdUseCase = GetCoffeeByIdUseCase(coffeeRepository)
    val addToCartUseCase = AddToCartUseCase(cartRepository)
    val checkPremiumStatusUseCase = CheckPremiumStatusUseCase(billingManager)

    val saveUserProfileUseCase = SaveUserProfileUseCase(userRepository)
    val getUserProfileUseCase = GetUserProfileUseCase(userRepository)
    val deleteUserProfileUseCase = DeleteUserProfileUseCase(userRepository)

    fun initializeAuth(activity: Activity) {
        if (_authDataSource == null) {
            _authDataSource = FirebaseAuthDataSource(context, activity)
        }
    }
}
