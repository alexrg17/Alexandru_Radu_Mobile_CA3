package com.example.alexandru_radu_ca3

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// Updated data class with drawable resource IDs
data class Room(
    val name: String,
    val temperature: String,
    val humidity: String,
    val resident: String,
    val age: Int,
    val preferredTemp: String,
    val preferredHumidity: String,
    val drawableResId: Int // Updated field for drawable resource ID
)

// Mock data with drawable resource IDs
val mockRooms = listOf(
    Room("Jerry's Bedroom", "14.9°C", "78.3%", "Jerry", 88, "14.9°C", "80%", R.drawable.jerry_bedroom),
    Room("Alice's Bedroom", "15°C", "77.7%", "Alice", 79, "15°C", "77%", R.drawable.alice_bedroom),
    Room("Paul's Bedroom", "15°C", "77.6%", "Paul", 82, "15°C", "77%", R.drawable.paul_bedroom),
    Room("Emma's Bedroom", "15°C", "76%", "Emma", 90, "15°C", "76%", R.drawable.emma_bedroom)
)

@Composable
fun HomePage() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Log homepage loading
    Log.d("HomePage", "Homepage loaded.")

    // Trigger Snackbar
    LaunchedEffect(Unit) {
        scope.launch {
            snackbarHostState.showSnackbar("Welcome to the Home Monitor App!")
            Log.d("HomePage", "Snackbar displayed: Welcome to the Home Monitor App!")
        }
    }

    // Dynamic gradient colors for the header
    val infiniteTransition = rememberInfiniteTransition(label = "Gradient Animation")
    val headerColor1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF6200EE),
        targetValue = Color(0xFF03DAC5),
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 4000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "Header Gradient Color 1"
    )
    val headerColor2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF03DAC5),
        targetValue = Color(0xFFFF5722),
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 4000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "Header Gradient Color 2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(headerColor1, headerColor2)
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Home Temperature Monitor",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                )
            }

            // Room Cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(mockRooms) { index, room ->
                    // Fade-in animation for each card
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = index * 100))
                    ) {
                        RoomCard(room)
                        Log.d("HomePage", "RoomCard displayed for: ${room.name}")
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun RoomCard(room: Room) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display image with rounded corners
                Image(
                    painter = painterResource(id = room.drawableResId),
                    contentDescription = "${room.name} Image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Temp: ${room.temperature} | Humidity: ${room.humidity}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }

            if (isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)

                    InfoRow(label = "Resident", value = "${room.resident} (${room.age} years old)")
                    InfoRow(label = "Preferred Temp", value = room.preferredTemp)
                    InfoRow(label = "Preferred Humidity", value = room.preferredHumidity)

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}