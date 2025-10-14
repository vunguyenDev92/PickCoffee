package com.example.androidmycoffee.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidmycoffee.domain.usecase.GetCoffeeListUseCase

class CoffeeViewModelFactory(
    private val getCoffeeListUseCase: GetCoffeeListUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoffeeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CoffeeViewModel(getCoffeeListUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
