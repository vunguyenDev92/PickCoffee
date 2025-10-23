package com.example.androidmycoffee.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidmycoffee.R

@Composable
fun PremiumPurchaseScreen(
    onContinue: (String) -> Unit,
    onClose: () -> Unit = {},
) {
    var selectedPlan by remember { mutableStateOf("yearly") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF210E5C), Color(0xFFB91438)),
                ),
            )
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Close button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onClose()
                    },
                contentAlignment = Alignment.TopEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF2E8AE5),
                    modifier = Modifier
                        .padding(end = 0.dp)
                        .size(40.dp),
                )
            }

            // Header content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_premium_placeholder),
                    contentDescription = "Premium",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Get Full Access",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Unlimited features, advanced filters & favorites",
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                PlanOption(
                    title = "Weekly",
                    subtitle = "Cancel Anytime",
                    price = "$13.99/week",
                    isSelected = selectedPlan == "weekly",
                    onClick = { selectedPlan = "weekly" },
                )
                PlanOption(
                    title = "Monthly",
                    subtitle = "Cancel Anytime",
                    price = "$13.99/month",
                    isSelected = selectedPlan == "monthly",
                    onClick = { selectedPlan = "monthly" },
                )
                PlanOption(
                    title = "Yearly",
                    subtitle = "Cancel Anytime",
                    price = "$23.99/year",
                    isSelected = selectedPlan == "yearly",
                    onClick = { selectedPlan = "yearly" },
                )
            }

            // Continue button
            Button(
                onClick = { onContinue(selectedPlan) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
            ) {
                Text("Continue", color = Color.White, fontSize = 18.sp)
            }

            // Footer
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            ) {
                Text(
                    "Terms of use",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                )
                Text(
                    "Restore",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                )
                Text(
                    "Privacy Policy",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
fun PlanOption(
    title: String,
    subtitle: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) Color(0xFFFF1744) else Color.White.copy(alpha = 0.1f)
    val textColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = textColor)
            Text(subtitle, color = textColor.copy(alpha = 0.8f), fontSize = 12.sp)
        }
        Text(price, color = textColor, fontWeight = FontWeight.Medium)
    }
}
