package com.example.androidmycoffee.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.domain.usecase.GetCoffeeListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CoffeeUiState(
    val coffees: List<Coffee> = emptyList(),
    val isLoading: Boolean = false,
)

class CoffeeViewModel(
    private val getAllCoffeeUseCase: GetCoffeeListUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoffeeUiState())
    val uiState: StateFlow<CoffeeUiState> = _uiState

    init {
        loadCoffees()
    }

    private fun loadCoffees() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val coffees = getAllCoffeeUseCase()
            _uiState.value = CoffeeUiState(coffees = coffees)
        }
    }
}
