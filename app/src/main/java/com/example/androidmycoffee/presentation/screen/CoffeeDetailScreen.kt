package com.example.androidmycoffee.presentation.screen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.presentation.ad.RewardedAdManager
import com.example.androidmycoffee.presentation.component.WatchAdsDialog
import com.example.androidmycoffee.presentation.viewmodel.CartViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun CoffeeDetailScreen(
    selectedCoffee: Coffee,
    cartViewModel: CartViewModel,
    onNavigate: (String) -> Unit,
) {
    val activity = LocalContext.current as Activity
    val event by cartViewModel.event.collectAsState()
    var showPremiumDialog by remember { mutableStateOf(false) }
    var showNoAdsDialog by remember { mutableStateOf(false) }
    var showWatchAdsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        when (event) {
            CartViewModel.CartEvent.NeedPremium -> {
                showPremiumDialog = true
            }
            CartViewModel.CartEvent.Added -> {
                onNavigate("cart")
            }
            null -> Unit
        }
        cartViewModel.resetEvent()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text("Coffee Detail Screen")
            Text("Selected: ${selectedCoffee.name}")
            Text("Price: $${selectedCoffee.price}")

            Button(onClick = {
                showWatchAdsDialog = true
            }) {
                Text("Add to Cart")
            }
        }
    }

    if (showPremiumDialog) {
        AlertDialog(
            onDismissRequest = { showPremiumDialog = false },
            title = {
                Text("Premium Required")
            },
            text = {
                Text("You need to upgrade to Premium to add more items to your cart. With Premium, you can add unlimited items!")
            },
            confirmButton = {
                Button(onClick = {
                    showPremiumDialog = false
                    onNavigate("premium")
                }) {
                    Text("Upgrade Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPremiumDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showNoAdsDialog) {
        AlertDialog(
            onDismissRequest = { showNoAdsDialog = false },
            title = {
                Text("Advertisement Required")
            },
            text = {
                Text("To add items to your cart, you need to watch an advertisement. Please wait for the ad to load and try again.")
            },
            confirmButton = {
                Button(onClick = {
                    showNoAdsDialog = false
                    RewardedAdManager.loadAd(activity)
                }) {
                    Text("Load Ad")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoAdsDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showWatchAdsDialog) {
        WatchAdsDialog(
            onDismiss = { showWatchAdsDialog = false },
            onWatch = {
                showWatchAdsDialog = false
                val adLoaded = RewardedAdManager.isAdLoaded()

                if (adLoaded) {
                    RewardedAdManager.showRewardedAd(activity) {
                        cartViewModel.addProduct(selectedCoffee)
                    }
                } else {
                    showNoAdsDialog = true
                }
            },
        )
    }
}
