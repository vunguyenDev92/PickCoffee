package com.example.androidmycoffee

import android.content.Context
import com.example.androidmycoffee.data.datasource.local.CoffeeLocalDataSourceImpl
import com.example.androidmycoffee.data.datasource.local.database.AppDatabase
import com.example.androidmycoffee.data.repository.CartRepositoryImpl
import com.example.androidmycoffee.data.repository.CoffeeRepositoryImpl
import com.example.androidmycoffee.domain.usecase.AddToCartUseCase
import com.example.androidmycoffee.domain.usecase.CheckPremiumStatusUseCase
import com.example.androidmycoffee.domain.usecase.GetCoffeeByIdUseCase
import com.example.androidmycoffee.domain.usecase.GetCoffeeListUseCase

class AppContainer(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val localDataSource = CoffeeLocalDataSourceImpl(db.coffeeDao())
    private val coffeeRepository = CoffeeRepositoryImpl(localDataSource)
    val cartRepository = CartRepositoryImpl()
    val getCoffeeListUseCase = GetCoffeeListUseCase(coffeeRepository)
    val getCoffeeByIdUseCase = GetCoffeeByIdUseCase(coffeeRepository)
    val addToCartUseCase = AddToCartUseCase(cartRepository)
    val checkPremiumStatusUseCase = CheckPremiumStatusUseCase()
}
