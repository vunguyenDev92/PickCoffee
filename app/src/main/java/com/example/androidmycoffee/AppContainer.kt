// AppContainer.kt
package com.example.androidmycoffee

import android.content.Context
import com.example.androidmycoffee.data.billing.BillingManager
import com.example.androidmycoffee.data.datasource.local.CoffeeLocalDataSourceImpl
import com.example.androidmycoffee.data.datasource.local.database.AppDatabase
import com.example.androidmycoffee.data.datasource.remote.FirebaseManager
import com.example.androidmycoffee.data.repository.CartRepositoryImpl
import com.example.androidmycoffee.data.repository.CoffeeRepositoryImpl
import com.example.androidmycoffee.data.repository.OrderRepositoryImpl
import com.example.androidmycoffee.domain.usecase.*

class AppContainer(context: Context) {
    // Local Database
    private val db = AppDatabase.getInstance(context)
    private val localDataSource = CoffeeLocalDataSourceImpl(db.coffeeDao())

    // Remote Data Source (Firebase or REST API)
    private val firebaseManager = FirebaseManager()
    private val remoteDataSource = CoffeeRemoteDataSourceFactory.create()

    // Repositories
    private val coffeeRepository = CoffeeRepositoryImpl(
        localDataSource = localDataSource,
        remoteDataSource = remoteDataSource,
    )

    val orderRepository = OrderRepositoryImpl(remoteDataSource)

    // Billing
    val billingManager = BillingManager(context).apply { initialize() }
    val cartRepository = CartRepositoryImpl(billingManager)

    // Use Cases - Coffee
    val getCoffeeListUseCase = GetCoffeeListUseCase(coffeeRepository)
    val getCoffeeByIdUseCase = GetCoffeeByIdUseCase(coffeeRepository)
    val syncCoffeeDataUseCase = SyncCoffeeDataUseCase(coffeeRepository)

    // Use Cases - Cart
    val addToCartUseCase = AddToCartUseCase(cartRepository)
    val checkPremiumStatusUseCase = CheckPremiumStatusUseCase(billingManager)

    // Use Cases - Order
    val createOrderUseCase = CreateOrderUseCase(orderRepository)
    val getOrderHistoryUseCase = GetOrderHistoryUseCase(orderRepository)

    // Firebase Manager (for seeding data)
    val firebase = firebaseManager
}
