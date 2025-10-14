package com.example.androidmycoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.androidmycoffee.presentation.screen.CoffeeScreen
import com.example.androidmycoffee.presentation.viewmodel.CoffeeViewModel
import com.example.androidmycoffee.presentation.viewmodel.CoffeeViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: CoffeeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as CoffeeApplication).appContainer
        val factory = CoffeeViewModelFactory(appContainer.getCoffeeListUseCase)
        viewModel = ViewModelProvider(this, factory)[CoffeeViewModel::class.java]
        setContent {
            CoffeeScreen(viewModel)
        }
    }
}
