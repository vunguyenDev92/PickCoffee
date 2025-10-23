package com.example.androidmycoffee.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidmycoffee.domain.model.CartItem
import com.example.androidmycoffee.presentation.component.PremiumDialog
import com.example.androidmycoffee.presentation.viewmodel.CartViewModel

@Composable
fun CoffeeCartScreen(
    viewModel: CartViewModel,
    onNavigateToPremium: () -> Unit,
) {
    val event by viewModel.event.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    var showPremiumDialog by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        when (event) {
            CartViewModel.CartEvent.NeedPremium -> {
                showPremiumDialog = true
            }
            CartViewModel.CartEvent.Added -> {}
            null -> Unit
        }
        viewModel.resetEvent()
    }

    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Your Cart",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (cartItems.size == 1) {
                    PremiumWarningCard(
                        onUpgradeToPremium = onNavigateToPremium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (cartItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Your cart is empty ☕")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(cartItems) { item ->
                            CartItemRow(item)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { /* TODO: Handle checkout logic */ },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Checkout")
                    }
                }
            }
        }
    }

    if (showPremiumDialog) {
        PremiumDialog(
            onDismiss = { showPremiumDialog = false },
            onUpgrade = {
                showPremiumDialog = false
                onNavigateToPremium()
            },
        )
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(item.coffee.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "x${item.quantity}  •  ${item.coffee.price}₫",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun PremiumWarningCard(
    onUpgradeToPremium: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Premium Required!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You can only add 1 item to cart with the free version. Upgrade to Premium to add unlimited items!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onUpgradeToPremium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Upgrade to Premium")
            }
        }
    }
}
