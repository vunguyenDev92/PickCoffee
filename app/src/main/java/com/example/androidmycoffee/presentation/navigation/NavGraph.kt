package com.example.androidmycoffee.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidmycoffee.AppContainer
import com.example.androidmycoffee.presentation.screen.CoffeeCartScreen
import com.example.androidmycoffee.presentation.screen.CoffeeDetailScreen
import com.example.androidmycoffee.presentation.screen.CoffeeScreen
import com.example.androidmycoffee.presentation.screen.CoffeeSettingScreen
import com.example.androidmycoffee.presentation.viewmodel.CoffeeViewModel
import com.example.androidmycoffee.presentation.viewmodel.CoffeeViewModelFactory

sealed class Screen(val route: String) {
    object CoffeeList : Screen("coffee_list")
    object CoffeeDetail : Screen("coffee_detail")
    object Cart : Screen("cart")
    object Settings : Screen("settings")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CoffeeList.route,
    ) {
        composable(Screen.CoffeeList.route) {
            val factory = CoffeeViewModelFactory(appContainer.getCoffeeListUseCase)
            val viewModel: CoffeeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
            CoffeeScreen(

                viewModel = viewModel,
                onNavigate = {
                    navController.navigate(it)
                },
            )
        }
        composable(Screen.CoffeeDetail.route) {
            CoffeeDetailScreen(onNavigate = {
                navController.navigate(it)
            })
        }
        composable(Screen.Cart.route) {
            CoffeeCartScreen()
        }
        composable(Screen.Settings.route) {
            CoffeeSettingScreen()
        }
    }
}
