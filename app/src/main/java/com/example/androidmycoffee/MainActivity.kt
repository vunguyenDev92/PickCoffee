package com.example.androidmycoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.androidmycoffee.presentation.navigation.AppNavGraph
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
            val navController = rememberNavController()
            val appContainer = (application as CoffeeApplication).appContainer
            AppNavGraph(navController = navController, appContainer = appContainer)
        }
    }
}
