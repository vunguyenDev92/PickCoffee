package com.example.androidmycoffee.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidmycoffee.AppContainer
import com.example.androidmycoffee.presentation.screen.AuthScreen
import com.example.androidmycoffee.presentation.screen.CoffeeCartScreen
import com.example.androidmycoffee.presentation.screen.CoffeeDetailScreen
import com.example.androidmycoffee.presentation.screen.CoffeeScreen
import com.example.androidmycoffee.presentation.screen.CoffeeSettingScreen
import com.example.androidmycoffee.presentation.screen.PremiumPurchaseScreen
import com.example.androidmycoffee.presentation.screen.ProfileScreen
import com.example.androidmycoffee.presentation.screen.SplashScreen
import com.example.androidmycoffee.presentation.viewmodel.AuthViewModel
import com.example.androidmycoffee.presentation.viewmodel.CartViewModel
import com.example.androidmycoffee.presentation.viewmodel.CoffeeViewModel
import kotlinx.coroutines.runBlocking

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object CoffeeList : Screen("coffee_list")
    object CoffeeDetail : Screen("coffee_detail")
    object Cart : Screen("cart")
    object Settings : Screen("settings")
    object Splash : Screen("splash")
    object Premium : Screen("premium")
    object Profile : Screen("profile")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
) {
    val authViewModel = remember {
        AuthViewModel(
            appContainer.authDataSource,
            appContainer.saveUserProfileUseCase,
        )
    }

    val cartViewModel = remember {
        CartViewModel(
            appContainer.addToCartUseCase,
            appContainer.checkPremiumStatusUseCase,
            appContainer.cartRepository,
        )
    }

    val startDestination = if (authViewModel.isSignedIn()) {
        Screen.CoffeeList.route
    } else {
        Screen.Splash.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigate = {
                    if (authViewModel.isSignedIn()) {
                        navController.navigate(Screen.CoffeeList.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                authDataSource = appContainer.authDataSource,
                onSignInSuccess = {
                    navController.navigate(Screen.CoffeeList.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.CoffeeList.route) {
            val viewModel = remember {
                CoffeeViewModel(appContainer.getCoffeeListUseCase)
            }
            CoffeeScreen(
                viewModel = viewModel,
                authViewModel = authViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onNavigateToDetail = { coffee ->
                    navController.navigate("${Screen.CoffeeDetail.route}/${coffee.id}")
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.CoffeeList.route) { inclusive = true }
                    }
                },
            )
        }

        composable("${Screen.CoffeeDetail.route}/{coffeeId}") { backStackEntry ->
            val coffeeId = backStackEntry.arguments?.getString("coffeeId")?.toIntOrNull()

            val selectedCoffee = remember {
                if (coffeeId != null) {
                    runBlocking {
                        appContainer.getCoffeeByIdUseCase(coffeeId)
                    }
                } else {
                    null
                }
            } ?: run {
                com.example.androidmycoffee.domain.model.Coffee(
                    id = 1,
                    name = "Coffee Not Found",
                    type = com.example.androidmycoffee.domain.model.TypeCoffee.LATTE,
                    price = java.math.BigDecimal("0.00"),
                )
            }

            CoffeeDetailScreen(
                selectedCoffee = selectedCoffee,
                cartViewModel = cartViewModel,
                onNavigate = {
                    navController.navigate(it)
                },
            )
        }

        composable(Screen.Cart.route) {
            CoffeeCartScreen(
                viewModel = cartViewModel,
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
                },
            )
        }

        composable(Screen.Settings.route) {
            CoffeeSettingScreen(
                authViewModel = authViewModel,
                onSignOut = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Premium.route) {
            PremiumPurchaseScreen(
                billingManager = appContainer.billingManager,
                onContinue = {
                    navController.navigateUp()
                },
                onClose = {
                    navController.navigateUp()
                },
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
            )
        }
    }
}
