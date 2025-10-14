package com.example.androidmycoffee.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidmycoffee.domain.model.Coffee
import com.example.androidmycoffee.presentation.viewmodel.CoffeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeScreen(viewModel: CoffeeViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("☕ Coffee Menu") }) },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                CoffeeList(state.coffees)
            }
        }
    }
}

@Composable
fun CoffeeList(coffees: List<Coffee>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(coffees) { coffee ->
            CoffeeItem(coffee)
        }
    }
}

@Composable
fun CoffeeItem(coffee: Coffee) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = coffee.name, style = MaterialTheme.typography.titleMedium)
            val priceText = coffee.price?.let { "Price: $" + String.format("%.2f", it) } ?: "Price: N/A"
            Text(text = priceText)
        }
    }
}
