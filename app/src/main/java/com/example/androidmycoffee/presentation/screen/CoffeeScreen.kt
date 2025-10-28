package com.example.androidmycoffee.presentation.screen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.androidmycoffee.domain.model.AuthState
import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.presentation.ad.BannerAdView
import com.example.androidmycoffee.presentation.ad.InterstitialAdManager
import com.example.androidmycoffee.presentation.viewmodel.AuthViewModel
import com.example.androidmycoffee.presentation.viewmodel.CoffeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeScreen(
    viewModel: CoffeeViewModel,
    authViewModel: AuthViewModel,
    onNavigate: (String) -> Unit,
    onNavigateToDetail: (Coffee) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onSignOut: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val currentUser = authViewModel.getCurrentUser()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("☕ Coffee Menu") },
                navigationIcon = {
                    if (currentUser != null && authState is AuthState.Authenticated) {
                        Row(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable { onNavigateToProfile() },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (!currentUser.photoUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = currentUser.photoUrl,
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape),
                                )
                            }
                            Text(
                                text = currentUser.displayName ?: "User",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                maxLines = 1,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToCart() }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                },
            )
        },
        bottomBar = { BannerAdView() },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                CoffeeList(state.coffees, onNavigateToDetail)
            }
        }
    }
}

@Composable
fun CoffeeList(coffees: List<Coffee>, onNavigateToDetail: (Coffee) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(coffees) { coffee ->
            CoffeeItem(coffee, onNavigateToDetail)
        }
    }
}

@SuppressLint("DefaultLocale", "ContextCastToActivity")
@Composable
fun CoffeeItem(coffee: Coffee, onNavigateToDetail: (Coffee) -> Unit) {
    val activity = LocalContext.current as Activity
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                InterstitialAdManager.showInterstitial(activity) {
                    onNavigateToDetail(coffee)
                }
            },
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = coffee.name, style = MaterialTheme.typography.titleMedium)
            val priceText = coffee.price?.let { "Price: $" + String.format("%.2f", it) } ?: "Price: N/A"
            Text(text = priceText)
        }
    }
}
