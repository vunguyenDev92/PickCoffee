package com.example.androidmycoffee.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidmycoffee.domain.model.CartItem
import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.domain.repository.CartRepository
import com.example.androidmycoffee.domain.usecase.AddToCartUseCase
import com.example.androidmycoffee.domain.usecase.CheckPremiumStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val addToCartUseCase: AddToCartUseCase,
    private val checkPremiumStatusUseCase: CheckPremiumStatusUseCase,
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _event = MutableStateFlow<CartEvent?>(null)
    val event: StateFlow<CartEvent?> = _event.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(item: CartItem) {
        viewModelScope.launch {
            val currentCartSize = cartRepository.getCartItems().size
            val isPremium = checkPremiumStatusUseCase()

            if (currentCartSize >= 1 && !isPremium) {
                _event.value = CartEvent.NeedPremium
                return@launch
            }

            // Add item to cart
            val result = addToCartUseCase(item)
            if (result.isSuccess) {
                _cartItems.value = cartRepository.getCartItems()
                _event.value = CartEvent.Added
            } else {
                _event.value = CartEvent.NeedPremium
            }
        }
    }

    fun addProduct(product: Coffee) {
        addToCart(CartItem(coffee = product))
    }

    fun resetEvent() {
        _event.value = null
    }

    fun loadCartItems() {
        viewModelScope.launch {
            _cartItems.value = cartRepository.getCartItems()
        }
    }

    init {
        loadCartItems()
    }

    sealed class CartEvent {
        object Added : CartEvent()
        object NeedPremium : CartEvent()
    }
}
