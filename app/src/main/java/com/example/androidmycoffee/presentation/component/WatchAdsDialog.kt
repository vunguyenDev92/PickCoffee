package com.example.androidmycoffee.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun WatchAdsDialog(
    onDismiss: () -> Unit,
    onWatch: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Watch Ads Required")
        },
        text = {
            Text("You need to watch ads to add items to your cart.")
        },
        confirmButton = {
            Button(onClick = onWatch) {
                Text("Watch Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
